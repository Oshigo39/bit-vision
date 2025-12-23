package com.chiho.bitvision.authority;

import javax.servlet.http.HttpServletRequest;

/**
 * 定义权限验证的标准契约
 * 策略模式的典型应用，允许不同的权限验证策略
 */
public interface AuthorityVerify {

    Boolean authorityVerify(HttpServletRequest request, String[] permissions);
}
