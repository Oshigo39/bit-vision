package com.chiho.bitvision.entity.vo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 找回密码VO模型
 */
@Data
public class FindPWVO {
    @Email(message = "邮箱格式不正确")
    String email;

    @NotNull(message = "邮箱验证码不能为空")
    Integer code;

    @NotBlank(message = "新密码不能为空")
    String newPassword;
}
