package net.jsrbc.adapter.common;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启动视图状态适配器
 * @author ZZZ on 2021/6/5 16:32
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ViewStatusAdapterConfigurer.class)
public @interface EnableViewStatusAdapter {}
