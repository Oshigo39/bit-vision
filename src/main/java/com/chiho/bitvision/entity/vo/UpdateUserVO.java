package com.chiho.bitvision.entity.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 用户可修改的信息VO
 */
@Data
public class UpdateUserVO {

    // 分布式系统需要用到这个userId
    private Long userId;

    @NotBlank(message = "昵称不可为空")
    private String nickName;

    private Long avatar;

    private Boolean sex;

    private String description;

    private Long defaultFavoritesId;
}
