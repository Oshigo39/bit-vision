package com.chiho.bitvision.entity.video;

import com.baomidou.mybatisplus.annotation.TableField;
import com.chiho.bitvision.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.Arrays;
import java.util.List;

/**
 * 视频分类实体类
 * 用于组织和管理不同类型的视频资源
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Type extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "分类名称不可为空")
    private String name;

    // 分类简介
    private String description;

    // 分类图标
    private String icon;

    // 是否开放
    private Boolean open;

    // 分类标签
    private String labelNames;

    // 排序字段
    private Integer sort;

    // 是否被使用
    @TableField(exist = false)
    private Boolean used;

    // 将labelNames分割为标签列表的方法
    public List<String> buildLabel(){
        return Arrays.asList(labelNames.split(","));
    }

}
