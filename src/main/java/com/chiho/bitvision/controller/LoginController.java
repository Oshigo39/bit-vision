package com.chiho.bitvision.controller;

import com.chiho.bitvision.entity.Captcha;
import com.chiho.bitvision.entity.user.User;
import com.chiho.bitvision.entity.vo.RegisterVO;
import com.chiho.bitvision.service.LoginService;
import com.chiho.bitvision.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/luckyjourney/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    /**
     * 前端获取图形验证码的GET请求
     * @param response HTTP响应体
     * @param uuId 前端传入的UUID
     * @throws IOException e
     */
    @GetMapping("/captcha.jpg/{uuId}")
    public void captcha(HttpServletResponse response, @PathVariable String uuId) throws IOException {
        loginService.captcha(uuId,response);
    }

    /**
     * 校验图形验证码，正确则发送邮箱验证码
     * @param captcha 图形验证码
     * @return msg
     * @throws Exception e
     */
    @PostMapping("/getCode")
    public R getCode(@RequestBody @Validated Captcha captcha) throws Exception {
        if (!loginService.getCode(captcha)) {
            return R.error().message("图形验证码错误");
        }
        return R.ok().message("邮箱验证码发送成功,请耐心等待");
    }

    /**
     * 校验邮箱验证码
     * @param email 邮箱
     * @param code 邮箱验证码
     * @return msg
     */
    @PostMapping("/check")
    public R checkCode(String email,Integer code){
        if (!loginService.checkCode(email,code)){
            return R.error().message("邮箱验证码校验失败");
        }
        return R.ok().message("邮箱验证码校验通过");
    }

    /**
     * 注册
     * @param registerVO 注册VO模型
     * @return 是否成功
     * @throws Exception e
     */
    @PostMapping("/register")
    public R register(@RequestBody @Validated RegisterVO registerVO) throws Exception{
        if (!loginService.register(registerVO)){
            return R.error().message("注册失败，邮箱验证码错误");
        }
        return R.ok().message("注册成功");
    }


}
