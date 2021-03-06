package net.jsrbc.ddd.core.old.command;

/**
 * 命令对象
 * @author ZZZ on 2020/8/4 21:16
 * @version 1.0
 */
public interface Command<RECEIVER> {
    /**
     * 执行命令
     * @param receiver 接收者
     * @return 执行完成后，返回的结果
     */
    ExecutionResult execute(RECEIVER receiver);
}
