package net.jsrbc.repository.mongodb.tools;

/**
 * 版本检查结果
 * @author ZZZ on 2021/5/26 11:55
 * @version 1.0
 */
public class ViewVersionCheckResult {

    /** 是否已经更新 */
    private final Boolean isUpdated;

    public ViewVersionCheckResult(Boolean isUpdated) {
        this.isUpdated = isUpdated;
    }

    public Boolean getUpdated() {
        return isUpdated;
    }
}
