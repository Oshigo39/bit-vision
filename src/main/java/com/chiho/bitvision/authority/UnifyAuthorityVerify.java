package com.chiho.bitvision.authority;

import com.chiho.bitvision.util.JwtUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 专门用于@PostMapping请求的权限验证器
 * 验证用户是否拥有指定的权限列表中的所有权限
 */
@Component(value = "postMappingAuthorityVerify")
public class UnifyAuthorityVerify extends DefaultAuthorityVerify{

    @Override
    public Boolean authorityVerify(HttpServletRequest request,
                                   String... permissions) {
        Long uId = JwtUtils.getUserId(request);
        for (String permission : permissions) {
            if (!AuthorityUtils.verify(uId,permission)) {
                return false;
            }
        }
        return true;
    }
}

