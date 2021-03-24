package net.jsrbc.ddd.core.model.aggregate;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * 聚合根
 * @author ZZZ on 2021/2/21 17:07
 * @version 1.0
 */
public abstract class Aggregate {

    /** 聚合根唯一标识 */
    private String id;

    /** 版本标识，自增 */
    private Long version;

    /** 标识聚合是否被删除 */
    private final Long deleted;

    /**
     * 校验聚合
     */
    public abstract void validate();

    /**
     * 更新版本号
     * @return 更换前的版本号
     */
    public Long updateVersion() {
        return this.version++;
    }

    /**
     * ID不存在时，设置ID
     * @param idSupplier ID生成器
     */
    public void setIdIfAbsent(Supplier<String> idSupplier) {
        if (this.id == null) this.id = idSupplier.get();
    }

    /**
     * 获取聚合根ID
     * @return 聚合根ID
     */
    public String getId() {
        return id;
    }

    /**
     * 获取聚合版本
     * @return 聚合版本
     */
    public Long getVersion() {
        return version;
    }

    /**
     * 获取聚合是否被删除
     * @return 删除的时间戳
     */
    public Long getDeleted() {
        return deleted;
    }

    public Aggregate(String id, Long version, Long deleted) {
        this.id = id;
        this.version = Optional.ofNullable(version).orElse(0L);
        this.deleted = deleted;
    }
}
