package net.jsrbc.ddd.core.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 校验器
 * @author ZZZ on 2021/2/21 17:30
 * @version 1.0
 */
public class Validator {
    /**
     * 不能为空，为空则抛出IllegalArgument异常
     * @param object 校验对象
     * @param message 校验消息
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 不能为空，为空则抛出IllegalArgument异常
     * @param collection 校验集合
     * @param message 校验消息
     */
    public static void notEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 判断是否满足正则表达
     * @param text 被校验文本
     * @param regex 正则表达式
     * @param message 校验信息
     */
    public static void match(String text, String regex, String message) {
        if (!text.matches(regex)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 两值必须相等
     * @param t1 值1
     * @param t2 值2
     * @param message 不相等的报错信息
     */
    public static <T> void eq(T t1, T t2, String message) {
        if (!Objects.equals(t1, t2)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 两值必须不相等
     * @param t1 值1
     * @param t2 值2
     * @param message 相等的报错信息
     */
    public static <T> void neq(T t1, T t2, String message) {
        if (Objects.equals(t1, t2)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 必须大于某值
     * @param target 校验的目标值
     * @param lowerBoundExclude 下界
     * @param message 校验消息
     */
    public static void gt(Integer target, Integer lowerBoundExclude, String message) {
        if (target == null || target <= lowerBoundExclude) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 必须小于某值
     * @param target 校验的目标值
     * @param upperBoundExclude 上界
     * @param message 校验消息
     */
    public static void lt(Integer target, Integer upperBoundExclude, String message) {
        if (target == null || target >= upperBoundExclude) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 必须介于上、下限之间，可以等于上、下界限
     * @param target 校验的目标值
     * @param lowerBoundInclude 下界
     * @param upperBoundInclude 上界
     * @param message 校验消息
     */
    public static void between(Integer target, Integer lowerBoundInclude, Integer upperBoundInclude, String message) {
        if (target == null || target < lowerBoundInclude || target > upperBoundInclude) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 判断是否为http地址
     * @param url 地址
     */
    public static void isHttpUrl(String url, String message) {
        if (!HttpUrlPatternHolder.HTTP_URL_PATTERN.matcher(url).matches()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 判断是否为有效的相对路径
     * @param uri 相对路径
     * @param message 消息
     */
    public static void isUri(String uri, String message) {
        if (!UriPatternHolder.URI_PATTERN.matcher(uri).matches()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 判断集合内对象是否唯一
     * @param collection 集合
     * @param message 错误消息
     */
    public static void isUnique(Collection<?> collection, String message) {
        if (collection == null) return;
        int size = new HashSet<>(collection).size();
        if (size != collection.size()) {
            throw new IllegalArgumentException(message);
        }
    }

    /** httpUrl正则表达式Holder */
    private static class HttpUrlPatternHolder {
        private final static Pattern HTTP_URL_PATTERN = Pattern.compile("^https?://[-A-Za-z0-9+&@#*/%?=~_|!:,.;]+[-A-Za-z0-9+&@#*/%=~_|]$");
    }

    private static class UriPatternHolder {
        private final static Pattern URI_PATTERN = Pattern.compile("^/[-A-Za-z0-9+&@#*/%?=~_|!:,.;]+[-A-Za-z0-9+&@#*/%=~_|]$");
    }
}
