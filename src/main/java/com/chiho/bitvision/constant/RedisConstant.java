package com.chiho.bitvision.constant;

/**
 * redis 存储常量
 */
public interface RedisConstant {

    // 邮箱验证码存储路径
    String EMAIL_CODE = "email:code:";

    // 邮箱验证码过期时间
    Long EMAIL_CODE_TIME = 300L;

    // 用户关注人
    String USER_FOLLOW = "user:follow:";

    // 用户粉丝
    String USER_FANS = "user:fans:";

    // 发布视频限流
    String VIDEO_LIMIT = "video:limit";
}
