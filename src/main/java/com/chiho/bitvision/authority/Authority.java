package com.chiho.bitvision.authority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 授权注解
 * 标记需要权限验证的接口
 * 自动调用BaseAuthority的 authorityVerify 方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authority {

    /**权限标识*/
    String[] value();

    /**具体执行校验类*/
    Class verify() default DefaultAuthorityVerify.class;
}
