package net.jsrbc.repository.mongodb.interpreter;

import org.springframework.data.mongodb.core.index.Index;

/**
 * 索引表达式
 * @author ZZZ on 2020/10/10 11:04
 * @version 1.0
 */
public interface IndexExpression {
    /**
     * 添加子表达式
     * @param indexExpression 子表达式
     */
    void addChild(IndexExpression indexExpression);

    /**
     * 解析索引
     * @param index 索引对象
     * @return 解析后的索引对象
     */
    Index interpret(Index index);
}
