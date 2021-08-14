package net.jsrbc.ddd.core.old.view;

/**
 * CQRS命令与视图分离时，常用的视图对象
 * @author ZZZ on 2021/3/17 14:29
 * @version 1.0
 */
public abstract class View {

    /** 主键 */
    private final String id;

    /** 版本号 */
    private final Long version;

    /** 更新时间 */
    private final Long updateTime;

    public View(String id, Long version, Long updateTime) {
        this.id = id;
        this.version = version;
        this.updateTime = updateTime;
    }

    public String getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public Long getUpdateTime() {
        return updateTime;
    }
}
