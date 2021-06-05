package net.jsrbc.event.rabbitmq;

import com.rabbitmq.client.MessageProperties;
import net.jsrbc.ddd.core.model.event.DomainEvent;
import net.jsrbc.ddd.core.model.event.EventSender;
import net.jsrbc.ddd.core.utils.JsonUtils;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 事件发布器
 * @author ZZZ on 2021/2/24 21:05
 * @version 1.0
 */
public class RabbitmqEventSender implements EventSender {

    private final Sender sender;

    private final BindingRegistry bindingRegistry;

    /**
     * 发布消息，能够监听事件是否发送失败
     * 需要订阅者能够处理重复事件
     * @param event 事件
     * @param onSuccess 确认事件，异步通知
     * @param onError 出现错误
     */
    @Override
    public <T extends DomainEvent> void send(T event, Consumer<T> onSuccess, BiConsumer<T, ? super Throwable> onError) {
        this.sender.sendWithPublishConfirms(createMsg(event))
                .subscribe(r -> {
                            if (!r.isAck()) {
                                onError.accept(event, new UnAckException(r.toString()));
                            } else {
                                onSuccess.accept(event);
                            }
                        },
                        e -> onError.accept(event, e));
    }

    @Override
    public void close() throws Exception {
        this.sender.close();
    }

    public RabbitmqEventSender(Sender sender, BindingRegistry bindingRegistry) {
        this.sender = sender;
        this.bindingRegistry = bindingRegistry;
    }

    /**
     * 创建消息
     * @param event 事件
     * @return 消息
     */
    private <T> Mono<OutboundMessage> createMsg(T event) {
        return Mono.just(event)
                .map(JsonUtils::toJson)
                .map(json -> new OutboundMessage(
                        this.bindingRegistry.getExchangeName(event.getClass()),
                        event.getClass().getSimpleName(),
                        MessageProperties.PERSISTENT_TEXT_PLAIN,
                        json.getBytes(StandardCharsets.UTF_8)));
    }
}
