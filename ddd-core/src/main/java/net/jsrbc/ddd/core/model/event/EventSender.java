package net.jsrbc.ddd.core.model.event;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 事件发送者
 * @author ZZZ on 2021-03-24 14:47
 * @version 1.0
 */
public interface EventSender extends AutoCloseable {
    /**
     * 发送事件
     * @param event 领域事件
     * @param onSuccess 成功回调
     * @param onError 错误回调
     * @param <T> 领域事件类型
     */
    <T extends DomainEvent> void send(T event, Consumer<T> onSuccess, BiConsumer<T, ? super Throwable> onError);
}
