package net.jsrbc.repository.mongodb.interpreter.impl;

import net.jsrbc.repository.mongodb.interpreter.IndexExpression;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.index.Index;

import java.util.Objects;

/**
 * 属性索引表达式
 * @author ZZZ on 2020/10/10 11:25
 * @version 1.0
 */
public class FieldIndexExpression implements IndexExpression {

    /** 属性 */
    private final String field;

    /** 顺序号 */
    private final String order;

    @Override
    public void addChild(IndexExpression indexExpression) {
        throw new UnsupportedOperationException("属性索引不支持设置子表达式");
    }

    @Override
    public Index interpret(Index index) {
        return index.on(field,
                Objects.equals(order, "desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC);
    }

    public FieldIndexExpression(String field, String order) {
        this.field = field;
        this.order = order;
    }
}
