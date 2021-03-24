package net.jsrbc.repository.mongodb.interpreter;

import net.jsrbc.repository.mongodb.interpreter.impl.ComposeIndexExpression;
import net.jsrbc.repository.mongodb.interpreter.impl.FieldIndexExpression;
import net.jsrbc.repository.mongodb.interpreter.impl.TTLIndexExpression;
import net.jsrbc.repository.mongodb.interpreter.impl.UniqueIndexExpression;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 索引表达式
 * @author ZZZ on 2020/10/10 15:18
 * @version 1.0
 */
public class IndexExpressionParser {

    /** 唯一索引模式 */
    private final static Pattern UNIQUE_PAT = Pattern.compile("^unique\\((.*)\\)$");

    /** TTL模式 */
    private final static Pattern TTL_PAT = Pattern.compile("^ttl:(\\d+\\w)\\((.*)\\)$");

    /**
     * 解析索引表达式
     * @param expression 索引表达式字符串
     * @return 表达式集合
     */
    public static List<IndexExpression> parse(String expression) {
        List<IndexExpression> results = new ArrayList<>();
        String[] indexes = expression.split(";");
        for (String fields : indexes) {
            // 去除空白符
            fields = fields.trim();
            // 1、判断是否为唯一索引
            Matcher uniqueMatcher = UNIQUE_PAT.matcher(fields);
            // 2、判断是否为TTL索引
            Matcher ttlMatcher = TTL_PAT.matcher(fields);
            // 3、开始解析
            if (uniqueMatcher.matches()) {
                UniqueIndexExpression uie = new UniqueIndexExpression();
                uie.addChild(parseCompose(uniqueMatcher.group(1)));
                results.add(uie);
            } else if (ttlMatcher.matches()) {
                String term = ttlMatcher.group(1);
                TTLIndexExpression tie = new TTLIndexExpression(Long.parseLong(term.substring(0, term.length() - 1)), term.substring(term.length() - 1));
                tie.addChild(parseCompose(ttlMatcher.group(2)));
                results.add(tie);
            } else {
                results.add(parseCompose(fields));
            }
        }
        return results;
    }

    /**
     * 解析复合索引
     * @param expression 复合索引表达式字符串
     * @return 复合索引集合
     */
    private static ComposeIndexExpression parseCompose(String expression) {
        ComposeIndexExpression cie = new ComposeIndexExpression();
        for (String field : expression.split(",")) {
            // 去除空白符
            field = field.trim();
            // 开始解析
            String[] type = field.split(":");
            if (type.length == 1) {
                cie.addChild(new FieldIndexExpression(type[0], null));
            } else {
                cie.addChild(new FieldIndexExpression(type[0], type[1]));
            }
        }
        return cie;
    }

    private IndexExpressionParser() {}
}
