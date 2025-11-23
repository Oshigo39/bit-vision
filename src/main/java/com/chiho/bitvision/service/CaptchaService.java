package com.chiho.bitvision.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chiho.bitvision.entity.Captcha;

import java.awt.image.BufferedImage;


/**
 * 系统验证码 服务接口
 */
public interface CaptchaService extends IService<Captcha> {

    // 生成图片的方法（java标准库生成图片）
    BufferedImage getCaptcha(String uuId);

    // 验证图形验证码的方法
    boolean validate(Captcha captcha) throws Exception;
}
