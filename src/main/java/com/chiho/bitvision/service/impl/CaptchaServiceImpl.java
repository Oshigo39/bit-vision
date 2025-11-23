package com.chiho.bitvision.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chiho.bitvision.constant.RedisConstant;
import com.chiho.bitvision.entity.Captcha;
import com.chiho.bitvision.exception.BaseException;
import com.chiho.bitvision.mapper.CaptchaMapper;
import com.chiho.bitvision.service.CaptchaService;
import com.chiho.bitvision.service.EmailService;
import com.chiho.bitvision.util.DateUtil;
import com.chiho.bitvision.util.RedisCacheUtil;
import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.Date;

@Service
public class CaptchaServiceImpl extends ServiceImpl<CaptchaMapper, Captcha> implements CaptchaService {

    @Autowired
    private Producer producer;

    // 邮件服务依赖，提供发送邮件的方法
    @Autowired
    private EmailService emailService;

    @Autowired
    private RedisCacheUtil redisCacheUtil;


    /**
     * 根据UUID生成验证码的方法
     * @param uuId UUID
     * @return JAVA原生的图片
     */
    @Override
    public BufferedImage getCaptcha(String uuId) {
        String code = this.producer.createText();
        Captcha captcha = new Captcha();
        captcha.setUuid(uuId);
        captcha.setCode(code);
        captcha.setExpireTime(DateUtil.addDateMinutes(new Date(),5));
        this.save(captcha);
        return producer.createImage(code);
    }

    /**
     * 验证用户提交的图形验证码，并在验证成功后生成6位数字邮箱验证码发送给用户
     * @param captcha
     * @return 是否验证成功
     * @throws Exception 异常
     */
    @Override
    public boolean validate(Captcha captcha) throws Exception {
        // 从传入的captcha对象获取邮箱地址
        String email = captcha.getEmail();
        // 用户输入的验证码
        final String code1 = captcha.getCode();
        // 使用传入对象的 uuid 作为查询条件，从数据库中查询对应的验证码记录
        captcha = this.getOne(new LambdaQueryWrapper<Captcha>().eq(Captcha::getUuid,captcha.getUuid()));
        if (captcha == null) throw new BaseException("UUID为空");
        // 无论是否验证成功，立即从数据库中移除该验证码记录（一次性使用 原则，提高安全性）
        this.remove(new LambdaQueryWrapper<Captcha>().eq(Captcha::getUuid, captcha.getUuid()));

        if (!captcha.getCode().equals(code1)){
            throw new BaseException("CODE不匹配");
        }
        if(captcha.getExpireTime().getTime()<=System.currentTimeMillis()){
            throw new BaseException("UUID已过期");
        }
        if (!code1.equals(captcha.getCode())) return false;     // 双重校验
        // 生成与发送邮箱验证码
        String code = getSixCode();
        // 设置带时间的Redis缓存
        redisCacheUtil.set(RedisConstant.EMAIL_CODE+email,code,RedisConstant.EMAIL_CODE_TIME);
        emailService.send(email,"注册验证码:"+code+",验证码5分钟之内有效");
        return true;
    }

    /**
     * 生成6位随机数验证码的方法
     * @return 随机验证码
     */
    public static String getSixCode(){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int code = (int) (Math.random()*10);
            builder.append(code);
        }
        return builder.toString();
    }
}
