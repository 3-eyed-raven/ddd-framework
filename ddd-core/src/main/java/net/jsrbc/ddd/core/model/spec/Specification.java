package net.jsrbc.ddd.core.model.spec;

/**
 * 查询规格
 * @author ZZZ on 2020/7/31 9:57
 * @version 1.0
 */
public abstract class Specification {

    /** 跳过的数量 */
    private final Integer skip;

    /** 查询的尺寸 */
    private final Integer size;

    public Specification(Integer skip, Integer size) {
        this.skip = skip;
        this.size = size;
    }

    public Integer getSkip() {
        return skip;
    }

    public Integer getSize() {
        return size;
    }
}
