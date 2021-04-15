package net.jsrbc.ddd.core.command;

/**
 * 命令操作执行结果
 * @author ZZZ on 2021/4/15 20:33
 * @version 1.0
 */
public class ExecutionResult {

    /** 执行的聚合ID */
    private final String id;

    /** 执行完后的版本号 */
    private final String version;

    public ExecutionResult(String id, String version) {
        this.id = id;
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }
}
