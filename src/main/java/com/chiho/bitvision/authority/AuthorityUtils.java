package com.chiho.bitvision.authority;

import lombok.Getter;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * 权限校验工具类
 */
public class AuthorityUtils {


    /**权限集合*/
    private static final Map<Long, Collection<String>> map = new HashMap<>();

    /**过滤权限集合*/
    private static final Set<String> filterPermission = new HashSet<>();

    /**全局权限校验类*/
    private static Class c;

    /**
     * 是否开启全局校验 默认为false不开启
     */
    private static Boolean globalVerify = false;

    /**
     * 是否开启 @PostMapping 全局校验 默认为false不开启
     * -- GETTER --
     *  获取 postAuthority 状态
     *
     */
    @Getter
    private static Boolean postAuthority = false;


    /**
     * 设置是否开启 @PostMapping 全局校验
     */
    public static void setPostAuthority(Boolean state,Class z){
        c = z;
        postAuthority = state;
    }

    /**
     * 重新初始化全局校验类Class
     */
    public static void cleanVerifyClass(){
        c = null;
    }


    /**
     * 获取全局权限校验类
     */
    public static Class getGlobalVerify(){
        return c;
    }

    /**
     * 开启全局校验
     */
    public static void setGlobalVerify(Boolean state,Object o){
        if (o == null){
            throw new NullPointerException();
        }else if (!(o instanceof AuthorityVerify)){
            throw new ClassCastException(o.getClass()+ "类型不是 AuthorityVerify 实现类");
        }

        c = o.getClass();
        globalVerify = state;
    }

    /**
     * 添加权限
     * @param uId 用户id
     * @param authority 权限集合
     */
    public static void setAuthority(Long uId,Collection<String> authority){
        map.put(uId,authority);
    }

    /**
     * 校验权限
     * @param uId
     */
    public static Boolean verify(Long uId,String authority){
        if (isEmpty(uId)) {
            return false;
        }
        return map.get(uId).contains(authority);
    }

    /**
     * 排除权限
     */
    public static void exclude(String... permissions){
        filterPermission.addAll(Arrays.asList(permissions));
    }

    /**
     * 是否有过滤权限
     */
    public static Boolean filterPermission(String permission){
        return filterPermission.contains(permission);
    }

    /**
     * 判空
     */
    public static Boolean isEmpty(Long uId){
        return ObjectUtils.isEmpty(map.get(uId));
    }
}
