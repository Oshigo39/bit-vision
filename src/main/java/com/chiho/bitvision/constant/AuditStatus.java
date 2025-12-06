package com.chiho.bitvision.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 审核状态
 */
public class AuditStatus {
    public static Integer SUCCESS = 0;
    public static Integer PROCESS = 1;
    public static Integer PASS = 2;
    public static Integer MANUAL = 3;

    private static Map<Integer,String> map = new HashMap<>();

    static {
        map.put(SUCCESS,"通过");
        map.put(PROCESS,"审核中");
        map.put(PASS,"失败");
        map.put(MANUAL,"需要人工审核");
    }

    public static String getName(Integer audit){
        return map.get(audit);
    }
}
