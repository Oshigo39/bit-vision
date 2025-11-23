package com.chiho.bitvision.entity.user;

import com.baomidou.mybatisplus.annotation.TableField;

import java.util.Set;

import com.chiho.bitvision.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
// import org.luckyjourney.config.QiNiuConfig;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Data
@EqualsAndHashCode(callSuper = false)
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // 用户昵称
    private String nickName;

    @Email
    private String email;

    @NotBlank(message = "密码不能为空")
    private String password;

    // 简介
    private String description;

    // true 为男，false为女
    private Boolean sex;

    // 用户默认收藏夹id
    private Long defaultFavoritesId;

    // 头像
    private Long avatar;

    /**
     * 非数据库字段，仅提供给临时业务使用，不参与CRUD
     */
    @TableField(exist = false)
    private Boolean each;

    @TableField(exist = false)
    private Set<String> roleName;

}
