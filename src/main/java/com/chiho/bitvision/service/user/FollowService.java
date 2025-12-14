package com.chiho.bitvision.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chiho.bitvision.entity.user.Follow;
import com.chiho.bitvision.entity.vo.BasePage;

import java.util.Collection;


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

    /**
     * 关注/取关
     * @param followsUserId 对方ID
     * @param userId 自己ID
     * @return ?
     */
    boolean follows(Long followsUserId, Long userId);

    /**
     * 获取自身关注的人员，并按照关注时间排序
     * @param userId userid
     * @param basePage page
     * @return collection
     */
    Collection<Long> getFollow(Long userId, BasePage basePage);

    /**
     * 获取粉丝人员且安排关注时间排序
     * @param userId userId
     * @param basePage base page
     * @return c
     */
    Collection<Long> getFans(Long userId, BasePage basePage);
}
