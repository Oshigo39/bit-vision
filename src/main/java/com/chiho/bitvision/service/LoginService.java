package com.chiho.bitvision.service;

import com.chiho.bitvision.entity.Captcha;
import com.chiho.bitvision.entity.vo.RegisterVO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface LoginService {

    /**
     * 生成图形验证码的接口
     * @param uuId 前端传入的UUID
     * @param response 响应给浏览器的HTTP内容
     * @throws IOException e
     */
    void captcha(String uuId, HttpServletResponse response) throws IOException;

    /**
     * 校验图形验证码，正确则发送邮箱验证码
     * @param captcha 图形验证码
     * @return ？
     * @throws Exception e
     */
    Boolean getCode(Captcha captcha) throws Exception;

    /**
     * 校验邮箱验证码
     * @param email 邮箱
     * @param code 邮箱验证码
     * @return ？
     */
    Boolean checkCode(String email, Integer code);

    /**
     * 注册账号
     * @param registerVO 注册VO模型
     * @return ?
     * @throws Exception e
     */
    Boolean register(RegisterVO registerVO) throws Exception;

}
