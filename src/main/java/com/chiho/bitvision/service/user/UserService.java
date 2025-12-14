package com.chiho.bitvision.service.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chiho.bitvision.entity.user.User;
import com.chiho.bitvision.entity.vo.*;

import java.util.Collection;
import java.util.List;

public interface UserService extends IService<User> {
    /**
     * 注册
     * @param registerVO 注册VO模型
     * @return 成功与否
     * @throws Exception e
     */
    boolean register(RegisterVO registerVO) throws Exception;

    /**
     * 找回密码
     * @param findPWVO 找回密码VO
     * @return ?
     */
    Boolean findPassword(FindPWVO findPWVO);

    /**
     * 获取指定用户的个人信息
     * 1.用户基本信息
     * 2.关注数量
     * 3.粉丝数量
     * @param userId 用户id
     * @return data
     */
    UserVO getInfo(Long userId);

    /**
     * 修改用户资料
     * @param userVO userVO
     */
    void updateUser(UpdateUserVO userVO);

    /**
     * 批量获取用户基本信息
     * @param userIds user's ids
     * @return list data with user
     */
    List<User> list(Collection<Long> userIds);

    /**
     * 关注/取关
     * @param followsUserId 对方ID
     * @param userId 自己ID
     * @return ?
     */
    boolean follows(Long followsUserId,Long userId);

    /**
     * 获取关注
     * @param basePage page of followed user
     * @param userId current user id
     * @return user's page data
     */
    Page<User> getFollows(BasePage basePage, Long userId);

    /**
     * 获取粉丝
     * @param userId userId
     * @param basePage base page
     * @return data
     */
    Page<User> getFans(Long userId, BasePage basePage);
}
