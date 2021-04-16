package net.jsrbc.repository.mongodb.tools;

/**
 * 检查结果
 * @author ZZZ on 2021/4/16 14:59
 * @version 1.0
 */
public final class CheckResult {

    /** 是否存在 */
    private final boolean exists;

    public CheckResult(boolean exists) {
        this.exists = exists;
    }

    public boolean isExists() {
        return exists;
    }
}
