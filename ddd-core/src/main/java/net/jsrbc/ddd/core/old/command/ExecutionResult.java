package net.jsrbc.ddd.core.old.command;

import net.jsrbc.ddd.core.aggregate.Aggregate;

/**
 * 命令操作执行结果
 * @author ZZZ on 2021/4/15 20:33
 * @version 1.0
 */
public class ExecutionResult {

    /** 执行的聚合ID */
    private final String id;

    /** 执行完后的版本号 */
    private final Long version;

    /**
     * 通过聚合构造执行结果
     * @param aggregate 聚合
     * @return 执行结果
     */
    public static ExecutionResult of(Aggregate aggregate) {
        return new ExecutionResult(aggregate.getId(), aggregate.getVersion());
    }

    /**
     * 通过ID构造执行结果
     * @param id 聚合ID
     * @return 执行结果
     */
    public static ExecutionResult of(String id) {
        return new ExecutionResult(id, null);
    }

    /**
     * 通过ID和版本号构造结果
     * @param id 聚合ID
     * @param version 版本号
     * @return 执行结果
     */
    public static ExecutionResult of(String id, Long version) {
        return new ExecutionResult(id, version);
    }

    public String getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    private ExecutionResult(String id, Long version) {
        this.id = id;
        this.version = version;
    }
}
