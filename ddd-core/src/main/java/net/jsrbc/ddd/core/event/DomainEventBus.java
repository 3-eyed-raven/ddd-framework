package net.jsrbc.ddd.core.event;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

/**
 * 领域事件发布器，基于发布订阅模式
 * @author ZZZ on 2021/2/22 14:35
 * @version 1.0
 */
public final class DomainEventBus {

    /** 监听者 */
    private final static Map<Class<? extends DomainEvent>, Set<Consumer<? super DomainEvent>>> SUBSCRIBERS = new ConcurrentHashMap<>();

    /**
     * 订阅领域事件，订阅所有类型时eventType可以设为DomainEvent
     * @param eventType 事件类型, DomainEvent代表监听所有领域事件
     * @param subscriber 订阅者
     */
    public static void subscribe(Class<? extends DomainEvent> eventType, Consumer<? super DomainEvent> subscriber) {
        SUBSCRIBERS.compute(eventType, (e, s) -> {
            Set<Consumer<? super DomainEvent>> set = s;
            if (set == null) set = new CopyOnWriteArraySet<>();
            set.add(subscriber);
            return set;
        });
    }

    /**
     * 取消订阅
     * @param eventType 事件类型
     * @param subscriber 订阅者
     */
    public static void unsubscribe(Class<? extends DomainEvent> eventType, Consumer<? super DomainEvent> subscriber) {
        SUBSCRIBERS.computeIfPresent(eventType, (e, s) -> {
            s.remove(subscriber);
            return s;
        });
    }

    /**
     * 发布领域事件
     * @param event 领域事件
     */
    public static void publish(DomainEvent event) {
        // 订阅所有领域事件
        if (SUBSCRIBERS.containsKey(DomainEvent.class)) {
            SUBSCRIBERS.get(DomainEvent.class).forEach(c -> c.accept(event));
        }
        // 订阅独立的领域事件
        if (SUBSCRIBERS.containsKey(event.getClass())) {
            SUBSCRIBERS.get(event.getClass()).forEach(c -> c.accept(event));
        }
    }

    private DomainEventBus() {}
}
