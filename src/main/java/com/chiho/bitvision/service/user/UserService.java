package com.chiho.bitvision.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chiho.bitvision.entity.user.User;
import com.chiho.bitvision.entity.vo.FindPWVO;
import com.chiho.bitvision.entity.vo.RegisterVO;
import com.chiho.bitvision.entity.vo.UpdateUserVO;
import com.chiho.bitvision.entity.vo.UserVO;

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
}
