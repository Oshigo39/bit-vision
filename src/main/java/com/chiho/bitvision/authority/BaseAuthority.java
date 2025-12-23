package com.chiho.bitvision.authority;

import com.chiho.bitvision.util.JwtUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 权限校验体系的核心实现类
 *
 */
public class BaseAuthority implements AuthorityVerify {
    @Override
    public Boolean authorityVerify(HttpServletRequest request,
                                   String[] permissions) {
        // JWT权限验证
        if (!JwtUtils.checkToken(request)) {
            return false;
        }
        // 获取当前用户权限
        Long uId = JwtUtils.getUserId(request);
        for (String permission : permissions) {
            if (!AuthorityUtils.verify(uId,permission)) {
                return false;
            }
        }
        return true;
    }
}
