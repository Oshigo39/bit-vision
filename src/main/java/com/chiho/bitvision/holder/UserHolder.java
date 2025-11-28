package com.chiho.bitvision.holder;

/**
 * 本地线程管道
 */
public class UserHolder {

    private static final ThreadLocal<Long> userThreadLocal = new ThreadLocal<>();

    // 添加userId
    public static void set(Object id){
        userThreadLocal.set(Long.valueOf(id.toString()));
    }
    // 获取
    public static Long get(){
        return userThreadLocal.get();
    }
    // 删除
    // 必须在请求结束后调用clear()方法清理，否则可能导致内存泄漏
    public static void clear(){
        userThreadLocal.remove();
    }
}
