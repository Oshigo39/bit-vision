package com.chiho.bitvision.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chiho.bitvision.constant.RedisConstant;
import com.chiho.bitvision.entity.Captcha;
import com.chiho.bitvision.entity.user.User;
import com.chiho.bitvision.entity.vo.FindPWVO;
import com.chiho.bitvision.entity.vo.RegisterVO;
import com.chiho.bitvision.exception.BaseException;
import com.chiho.bitvision.service.CaptchaService;
import com.chiho.bitvision.service.LoginService;
import com.chiho.bitvision.service.user.UserService;
import com.chiho.bitvision.util.RedisCacheUtil;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class LoginServiceImpl implements LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginServiceImpl.class);

    private static final String SALT = "oshigo39";

    @Autowired
    private UserService userService;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Override
    public void captcha(String uuId, HttpServletResponse response) throws IOException {
        // 使用Spring自带的对象工具进行空判断
        if (ObjectUtils.isEmpty(uuId)) throw new IllegalArgumentException("UUID不能为空");
        response.setHeader("Cache-Control","no-store, no-cache");
        response.setContentType("image/jpeg");
        BufferedImage image = captchaService.getCaptcha(uuId);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image,"jpg",out);
        IOUtils.closeQuietly(out);
    }

    @Override
    public Boolean getCode(Captcha captcha) throws Exception {
        // 校验图形验证码
        return captchaService.validate(captcha);
    }

    @Override
    public Boolean checkCode(String email, Integer code) {
        if (ObjectUtils.isEmpty(email) || ObjectUtils.isEmpty(code))
            throw new BaseException("参数为空");

        final  Object o = redisCacheUtil.get(RedisConstant.EMAIL_CODE + email);
        log.info("邮箱验证码的内容为：{}",o);
        if(o == null) throw new BaseException("邮箱验证码已过期或者不存在");
        if (!code.toString().equals(o.toString()))
            throw new BaseException("邮箱验证码不正确");
        return true;
    }

    @Override
    public Boolean register(RegisterVO registerVO) throws Exception {
        // 注册成功后删除图形验证码
        if (userService.register(registerVO)){
            captchaService.removeById(registerVO.getUuid());
            return true;
        }
        return false;
    }

    @Override
    public User login(User user) {
        // 保存用户输入的原始密码和邮箱
        final String inputPassword = user.getPassword();
        final String inputEmail = user.getEmail();

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 根据邮箱查询用户信息，使用不同的变量接收
        User dbUser = userService.getOne(wrapper.eq(User::getEmail, inputEmail));

        if (ObjectUtils.isEmpty(dbUser)){
            throw new BaseException("没有该账号");
        }

        // todo 后续可以使用安全性更高的加密方式
        // 调试日志
        log.info("输入密码: {}", inputPassword);
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + inputPassword).getBytes());
        log.info("加密后的密码: {}", encryptPassword);
        log.info("数据库中的密码: {}", dbUser.getPassword());

        // 正确比较：加密后的输入密码 vs 数据库中的加密密码
        if (!encryptPassword.equals(dbUser.getPassword())){
            throw new BaseException("密码错误");
        }

        // 脱敏操作
        dbUser.setPassword(null);
        return dbUser;
    }

    @Override
    public Boolean findPassword(FindPWVO findPWVO) {
        return userService.findPassword(findPWVO);
    }
}
