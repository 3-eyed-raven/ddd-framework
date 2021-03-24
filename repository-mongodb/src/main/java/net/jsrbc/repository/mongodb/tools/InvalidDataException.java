package net.jsrbc.repository.mongodb.tools;

/**
 * 版本冲突异常
 * @author ZZZ on 2020/9/22 14:56
 * @version 1.0
 */
public class InvalidDataException extends RuntimeException {

    public InvalidDataException() {
        super();
    }

    public InvalidDataException(String message) {
        super(message);
    }
}
