package com.chiho.bitvision.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chiho.bitvision.entity.user.User;
import com.chiho.bitvision.entity.vo.FindPWVO;
import com.chiho.bitvision.entity.vo.RegisterVO;

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
}
