package net.jsrbc.ddd.core.model.event;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 事件存储抽象
 * @author ZZZ on 2021-03-24 15:24
 * @version 1.0
 */
public abstract class AbstractEventStore {

    /** 定时器 */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

    /** 事件存储名 */
    protected final static String EVENT_STORE = "eventStore";

    /** 事件状态属性名 */
    protected final static String EVENT_STATUS_FIELD = "eventStatus";

    /** 投递时间属性名 */
    protected final static String DELIVERY_TIME = "deliveryTime";

    /** 事件发送者 */
    private final EventSender eventSender;

    /**
     * 错误处理器
     */
    abstract Consumer<? super Throwable> errorHandler();

    /**
     * 获取事件并标记为发送状态
     * @param deliveryTime 发送时间
     * @return 事件
     */
    abstract protected DomainEvent getAndMarkSending(Long deliveryTime);

    /**
     * 事件标记为等待状态
     * @param event 事件
     */
    abstract protected void markPending(DomainEvent event);

    /**
     * 标记为发送成功状态
     * @param event 事件
     */
    abstract protected void markSuccess(DomainEvent event);

    /**
     * 标记发送超时的事件为等待发送状态
     * @param deliveryExpiredTime 投递超时的认定时间
     */
    abstract protected void markSendingTimeoutToPending(Long deliveryExpiredTime);

    /**
     * 删除发送成功的事件
     */
    abstract protected void deleteSuccessEvent();

    /**
     * 写入事件
     * @param event 事件
     */
    abstract protected void insertEvent(DomainEvent event);

    /** 订阅所有事件，直接发送，发送失败则存事件存储 */
    public void init() {
        DomainEventPublisher.subscribe(DomainEvent.class, this::sendOrSave);
        this.scheduler.scheduleAtFixedRate(this::republish, 0, 1, TimeUnit.MINUTES);
        this.scheduler.scheduleAtFixedRate(this::clearTimeoutEvent, 0, 1, TimeUnit.DAYS);
        this.scheduler.scheduleAtFixedRate(this::clearSuccessEvent, 0, 1, TimeUnit.DAYS);
    }

    public AbstractEventStore(EventSender eventSender) {
        this.eventSender = eventSender;
    }

    /**
     * 重发失败的事件
     */
    private void republish() {
        while (!Thread.currentThread().isInterrupted()) {
            // 取出一个事件
            DomainEvent event = getAndMarkSending(System.currentTimeMillis());
            if (event == null) break;
            // 发布事件，成功则标记为SUCCESS，失败则标记为PENDING重发
            this.eventSender.send(event, this::markSuccess, (evt, ex) -> {
                this.markPending(evt);
                errorHandler().accept(ex);
            });
        }
    }

    /**
     * 清除发送超时的事件
     */
    private void clearTimeoutEvent() {
        this.markSendingTimeoutToPending(Instant.now().plus(-1, ChronoUnit.DAYS).toEpochMilli());
    }

    /**
     * 清除已经成功的事件
     */
    private void clearSuccessEvent() {
        this.deleteSuccessEvent();
    }


    /**
     * 发送事件
     * @param event 事件
     */
    private void sendOrSave(DomainEvent event) {
        event.setDeliveryTime(System.currentTimeMillis());
        this.eventSender.send(event, e -> {}, (evt, ex) -> {
            this.insertEvent(event);
            errorHandler().accept(ex);
        });
    }
}
