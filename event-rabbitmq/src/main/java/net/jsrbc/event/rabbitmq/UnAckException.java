package net.jsrbc.event.rabbitmq;

/**
 * 未被确认异常
 * @author ZZZ on 2021-03-24 16:00
 * @version 1.0
 */
public class UnAckException extends RuntimeException {
    public UnAckException(String message) {
        super(message);
    }
}
