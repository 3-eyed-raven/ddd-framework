package net.jsrbc.ddd.core.event;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 事件存储抽象
 * ！！！注意需要手动关闭事件存储，防止内存泄露
 * @author ZZZ on 2021-03-24 15:24
 * @version 1.0
 */
public abstract class EventStore implements AutoCloseable {

    /** 定时器 */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

    /** 事件存储名 */
    protected final static String EVENT_STORE_COLLECTION = "eventStore";

    /** 事件状态属性名 */
    protected final static String EVENT_STATUS_KEY = "eventStatus";

    /** 投递时间属性名 */
    protected final static String SEND_TIME_KEY = "sendTime";

    /** 事件发送者 */
    private final EventSender eventSender;

    /** 订阅所有事件，直接发送，发送失败则存事件存储 */
    public void init() {
        DomainEventBus.subscribe(DomainEvent.class, this::sendOrSave);
        this.scheduler.scheduleAtFixedRate(this::resend, 0, 1, TimeUnit.MINUTES);
        this.scheduler.scheduleAtFixedRate(this::resetExpiredEvent, 0, 1, TimeUnit.DAYS);
        this.scheduler.scheduleAtFixedRate(this::removeSucceededEvent, 0, 1, TimeUnit.DAYS);
    }

    /**
     * 错误处理器
     */
    abstract protected Consumer<? super Throwable> errorHandler();

    /**
     * 获取事件并标记为发送状态
     * @param sendTime 发送时间
     * @return 事件
     */
    abstract protected DomainEvent findPendingEventAndMarkSending();

    /**
     * 事件标记为等待状态
     * @param event 事件
     */
    abstract protected void markEventPending(DomainEvent event);

    /**
     * 标记为发送成功状态
     * @param event 事件
     */
    abstract protected void markEventSucceeded(DomainEvent event);

    /**
     * 标记发送超时的事件为等待发送状态
     * @param expiredTime 发送超时的认定时间
     */
    abstract protected void markExpiredEventAsPending(Long expiredTime);

    /**
     * 删除发送成功的事件
     */
    abstract protected void removeSucceededEvent();

    /**
     * 保存事件至事件存储
     * @param event 事件
     */
    abstract protected void saveEvent(DomainEvent event);

    @Override
    public void close() {
        this.scheduler.shutdownNow();
    }

    public EventStore(EventSender eventSender) {
        this.eventSender = eventSender;
    }

    /**
     * 重发失败的事件
     */
    private void resend() {
        while (!Thread.currentThread().isInterrupted()) {
            // 取出一个事件
            DomainEvent event = findPendingEventAndMarkSending();
            if (event == null) break;
            // 发布事件，成功则标记为SUCCESS，失败则标记为PENDING重发
            this.eventSender.send(event, this::markEventSucceeded, (evt, ex) -> {
                this.markEventPending(evt);
                errorHandler().accept(ex);
            });
        }
    }

    /**
     * 清除发送超时的事件
     */
    private void resetExpiredEvent() {
        this.markExpiredEventAsPending(Instant.now().plus(-1, ChronoUnit.DAYS).toEpochMilli());
    }

    /**
     * 发送事件，发送失败则保存进数据库
     * @param event 事件
     */
    private void sendOrSave(DomainEvent event) {
        event.setSendTime(System.currentTimeMillis());
        this.eventSender.send(event, e -> {}, (evt, ex) -> {
            this.saveEvent(event);
            errorHandler().accept(ex);
        });
    }
}
