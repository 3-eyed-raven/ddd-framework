package net.jsrbc.ddd.core.model.event;

import java.util.Optional;

/**
 * 领域事件
 * @author ZZZ on 2021/2/22 14:15
 * @version 1.0
 */
public abstract class DomainEvent {

    /** 事件ID */
    private String id;

    /** 事件状态 */
    private EventStatus eventStatus;

    /** 事件发生时间 */
    private final Long occurredTime;

    /** 投递时间 */
    private Long deliveryTime;

    public DomainEvent(String id, EventStatus eventStatus, Long occurredTime, Long deliveryTime) {
        this.id = id;
        this.eventStatus = Optional.ofNullable(eventStatus).orElse(EventStatus.PENDING);
        this.occurredTime = Optional.ofNullable(occurredTime).orElse(System.currentTimeMillis());
        this.deliveryTime = deliveryTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDeliveryTime(Long deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public void setEventStatus(EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }

    public String getId() {
        return id;
    }

    public EventStatus getEventStatus() {
        return eventStatus;
    }

    public Long getOccurredTime() {
        return occurredTime;
    }

    public Long getDeliveryTime() {
        return deliveryTime;
    }
}
