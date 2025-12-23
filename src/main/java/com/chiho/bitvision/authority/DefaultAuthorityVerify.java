package com.chiho.bitvision.authority;

import javax.servlet.http.HttpServletRequest;

/**
 * 默认权限验证的类-默认放行
 */
public class DefaultAuthorityVerify implements AuthorityVerify {

    @Override
    public Boolean authorityVerify(HttpServletRequest request,String[] permissions) {
        return true;
    }

}
