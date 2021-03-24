package net.jsrbc.event.rabbitmq;

import reactor.core.publisher.Flux;
import reactor.rabbitmq.ExchangeSpecification;
import reactor.rabbitmq.Sender;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.*;

/**
 * 绑定注册对象
 * @author ZZZ on 2021/2/24 16:58
 * @version 1.0
 */
public class BindingRegistry {

    /**
     * 注册映射表
     * String为路由routingKey
     * Tuple2中，T1为交换机名，T2为队列名
     */
    private final Map<String, Tuple2<String, String>> registryMap = new HashMap<>();

    private final RabbitmqProperties rabbitmqProperties;

    /**
     * 绑定事件与聚合
     * @param aggregateClass 聚合类型
     * @param eventClass 事件类型
     */
    public BindingRegistry bind(Class<?> aggregateClass, Class<?> eventClass, Class<?>... eventClasses) {
        // 组合事件类型
        List<Class<?>> list = new ArrayList<>();
        list.add(eventClass);
        list.addAll(Arrays.asList(eventClasses));
        // 遍历
        for (Class<?> ec : list) {
            registryMap.put(ec.getSimpleName(), Tuples.of(
                    composeName(rabbitmqProperties.getExchangePrefix(), aggregateClass.getSimpleName()),
                    composeName(rabbitmqProperties.getQueuePrefix(), ec.getSimpleName())));
        }
        return this;
    }

    /**
     * 绑定事件与交换机、队列
     * @param exchangeName 交换机名称
     * @param eventClass 事件类型
     */
    public BindingRegistry bind(String exchangeName, Class<?> eventClass, Class<?>... eventClasses) {
        // 组合事件类型
        List<Class<?>> list = new ArrayList<>();
        list.add(eventClass);
        list.addAll(Arrays.asList(eventClasses));
        // 遍历
        for (Class<?> ec : list) {
            registryMap.put(ec.getSimpleName(),
                    Tuples.of(exchangeName, composeName(rabbitmqProperties.getQueuePrefix(), ec.getSimpleName())));
        }
        return this;
    }

    /**
     * 声明交换机
     * @param sender sender对象
     */
    void declareExchange(Sender sender) {
        Flux.fromIterable(registryMap.values())
                .map(Tuple2::getT1)
                .distinct()
                .flatMap(ex -> sender.declareExchange(ExchangeSpecification.exchange(ex).durable(true)))
                .subscribe();
    }

    /**
     * 根据事件获取路由键
     * @param eventClass 事件类型
     * @return 路由键
     */
    String getRoutingKey(Class<?> eventClass) {
        if (!registryMap.containsKey(eventClass.getSimpleName())) {
            throw new IllegalArgumentException("event is not bind, extends AbstractRabbitmqConfigurer and bind first");
        }
        return eventClass.getSimpleName();
    }

    /**
     * 根据事件获取队列名
     * @param eventClass 事件类型
     * @return 队列名
     */
    String getQueueName(Class<?> eventClass) {
        if (!registryMap.containsKey(eventClass.getSimpleName())) {
            throw new IllegalArgumentException("event is not bind, extends AbstractRabbitmqConfigurer and bind first");
        }
        return registryMap.get(eventClass.getSimpleName()).getT2();
    }

    /**
     * 根据事件获取交换机名
     * @param eventClass 事件类型
     * @return 交换机名
     */
    String getExchangeName(Class<?> eventClass) {
        if (!registryMap.containsKey(eventClass.getSimpleName())) {
            throw new IllegalArgumentException("event is not bind, extends AbstractRabbitmqConfigurer and bind first");
        }
        return registryMap.get(eventClass.getSimpleName()).getT1();
    }

    public BindingRegistry(RabbitmqProperties rabbitmqProperties) {
        this.rabbitmqProperties = rabbitmqProperties;
    }

    /**
     * 组合名称
     * @param prefix 前缀
     * @param name 待组合名称
     * @return 组合名称
     */
    private static String composeName(String prefix, String name) {
        if (prefix != null && !prefix.isEmpty()) return prefix + "." + name;
        return name;
    }
}
