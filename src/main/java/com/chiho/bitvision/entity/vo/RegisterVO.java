package com.chiho.bitvision.entity.vo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * 用户注册 VO
 */
@Data
public class RegisterVO {

    @Email(message = "邮箱不能为空")
    private String email;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "邮箱验证码不能为空")
    private String code;

    @NotBlank(message = "UUID不能为空")
    private String uuid;

    @NotBlank(message = "用户昵称不能为空")
    private String nickName;
}
