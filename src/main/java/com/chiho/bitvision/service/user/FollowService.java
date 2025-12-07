package com.chiho.bitvision.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chiho.bitvision.entity.user.Follow;


public interface FollowService extends IService<Follow> {
    /**
     * 获取关注数量
     * @param userId userid
     * @return number
     */
    long getFollowCount(Long userId);

    /**
     * 获取粉丝数量
     * @param userId userid
     * @return number
     */
    long getFansCount(Long userId);
}
