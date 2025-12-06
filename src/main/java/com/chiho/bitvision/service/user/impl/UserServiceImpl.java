package com.chiho.bitvision.service.user.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chiho.bitvision.constant.RedisConstant;
import com.chiho.bitvision.entity.user.Favorites;
import com.chiho.bitvision.entity.user.User;
import com.chiho.bitvision.entity.vo.FindPWVO;
import com.chiho.bitvision.entity.vo.RegisterVO;
import com.chiho.bitvision.exception.BaseException;
import com.chiho.bitvision.mapper.user.UserMapper;
import com.chiho.bitvision.service.user.FavoritesService;
import com.chiho.bitvision.service.user.UserService;
import com.chiho.bitvision.util.DateUtil;
import com.chiho.bitvision.util.RedisCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final String SALT = "oshigo39";

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private FavoritesService favoritesService;

    /**
     * 注册
     * @param registerVO 注册VO模型
     * @return ?
     * @throws Exception e
     */
    @Override
    public boolean register(RegisterVO registerVO) throws Exception {
        // 邮箱是否存在
        final int count = (int) count(new LambdaQueryWrapper<User>().eq(User::getEmail, registerVO.getEmail()));
        if (count == 1){
            throw new BaseException("邮箱已经被注册");
        }
        final String code = registerVO.getCode();
        final Object o = redisCacheUtil.get(RedisConstant.EMAIL_CODE + registerVO.getEmail());
        if (o == null){
            throw new BaseException("邮箱验证码为空");
        }
        if (!code.equals(o)){
            return false;
        }
        // 初始化User相关的信息并保存
        final User user = new User();
        user.setNickName(registerVO.getNickName());
        user.setEmail(registerVO.getEmail());
        user.setDescription("这个人很懒...");
        // 密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex(
                (SALT + registerVO.getPassword()).getBytes()
        );
        user.setPassword(encryptPassword);
        user.setGmtCreated(new Date());
        save(user);

        // 创建默认收藏夹并存进数据库
        final Favorites favorites = new Favorites();
        favorites.setUserId(user.getId());
        favorites.setName("default favorites");
        favoritesService.save(favorites);

        // 这里如果单独抽出一个用户配置表就好了,但是没有必要再搞个表
        user.setDefaultFavoritesId(favorites.getId());
        updateById(user);
        return true;
    }

    /**
     * 找回密码
     * @param findPWVO 找回密码VO
     * @return ?
     */
    @Override
    public Boolean findPassword(FindPWVO findPWVO) {
        final Object o = redisCacheUtil.get(RedisConstant.EMAIL_CODE + findPWVO.getEmail());
        if (o == null) return false;
        // 校验邮箱验证码（对象o转换成字符串后再parseInt()成int类型）
        if (Integer.parseInt(o.toString())
                != findPWVO.getCode()) return false;
        // 修改
        final User user = new User();
        user.setEmail(findPWVO.getEmail());
        // 密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex(
                (SALT + findPWVO.getNewPassword()).getBytes()
        );
        user.setPassword(encryptPassword);
        update(user,new UpdateWrapper<User>()
                .lambda()
                .set(User::getPassword,encryptPassword)
                .set(User::getGmtUpdated,new Date())
                .eq(User::getEmail,findPWVO.getEmail()));
        return true;
    }
}
