package net.jsrbc.ddd.core.event;

/**
 * 事件状态
 * @author ZZZ on 2021/2/22 14:20
 * @version 1.0
 */
public enum EventStatus {

    /** 等待发送 */
    PENDING,

    /** 发送中 */
    SENDING,

    /** 事件发送成功 */
    SUCCESS
}
