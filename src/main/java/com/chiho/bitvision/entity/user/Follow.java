package com.chiho.bitvision.entity.user;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 关注表
 * 表中字段的关系是user_id -> follow_id
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Follow implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 被关注的id
    private Long followId;

    // 粉丝用户id
    private Long userId;

    // 告诉MP，该字段在insert时，自动填充数值
    @TableField(fill = FieldFill.INSERT)
    private Data gmtCreated;

}
