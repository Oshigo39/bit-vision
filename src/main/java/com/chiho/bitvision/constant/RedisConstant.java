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

    // 系统分类库，用于查询分类下的视频随机获取
    String SYSTEM_TYPE_STOCK = "system:type:stock:";

    // 系统视频库,每个公开的都会存在这
    String SYSTEM_STOCK = "system:stock:";

    // 发件箱
    String OUT_FOLLOW = "out:follow:feed:";

    // 收件箱
    String IN_FOLLOW = "in:follow:feed:";
}
