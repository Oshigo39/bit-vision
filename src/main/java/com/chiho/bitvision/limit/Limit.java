package com.chiho.bitvision.limit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口限流控制的java自定义注解类
 */
@Target({ElementType.METHOD})   // 作用目标：方法
@Retention(RetentionPolicy.RUNTIME) // 保留策略：运行时
public @interface Limit {

    // 指定时间内允许的最大请求次数
    int limit() default 0;

    // 时间窗口
    long time() default 0;

    // 限流key，区分不同限流规则
    String key() default "";

    // 触发限流时返回的提示信息
    String msg() default "系统服务繁忙";
}
