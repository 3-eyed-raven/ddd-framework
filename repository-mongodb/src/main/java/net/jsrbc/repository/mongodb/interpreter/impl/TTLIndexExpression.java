package net.jsrbc.repository.mongodb.interpreter.impl;

import net.jsrbc.repository.mongodb.interpreter.IndexExpression;
import org.springframework.data.mongodb.core.index.Index;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 过期索引表达式
 * @author ZZZ on 2020/10/10 11:43
 * @version 1.0
 */
public class TTLIndexExpression implements IndexExpression {

    private final Long amount;

    private final String unit;

    private final List<IndexExpression> expressions = new ArrayList<>();

    @Override
    public void addChild(IndexExpression indexExpression) {
        expressions.add(indexExpression);
    }

    @Override
    public Index interpret(Index index) {
        expressions.forEach(e -> e.interpret(index));
        index.expire(amount, convert(unit));
        return index;
    }

    public TTLIndexExpression(Long amount, String unit) {
        this.amount = amount;
        this.unit = unit;
    }

    /**
     * 简写时间单位转换
     * @param shortUnit 简写单位
     * @return 实际单位
     */
    private static TimeUnit convert(String shortUnit) {
        switch (shortUnit) {
            case "s":
                return TimeUnit.SECONDS;
            case "m":
                return TimeUnit.MINUTES;
            case "h":
                return TimeUnit.HOURS;
            case "d":
                return TimeUnit.DAYS;
            default:
                throw new IllegalArgumentException("不支持的ttl单位");
        }
    }
}
