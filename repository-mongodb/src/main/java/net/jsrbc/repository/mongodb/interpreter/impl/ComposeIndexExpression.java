package net.jsrbc.repository.mongodb.interpreter.impl;

import net.jsrbc.repository.mongodb.interpreter.IndexExpression;
import org.springframework.data.mongodb.core.index.Index;

import java.util.ArrayList;
import java.util.List;

/**
 * 复合索引表达式
 * @author ZZZ on 2020/10/10 11:25
 * @version 1.0
 */
public class ComposeIndexExpression implements IndexExpression {

    private final List<IndexExpression> expressions = new ArrayList<>();

    @Override
    public void addChild(IndexExpression indexExpression) {
        expressions.add(indexExpression);
    }

    @Override
    public Index interpret(Index index) {
        expressions.forEach(e -> e.interpret(index));
        return index;
    }
}
