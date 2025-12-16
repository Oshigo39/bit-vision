package com.chiho.bitvision.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * 用户兴趣模型的初始化数据结构
 * 1. 用于批量设置用户的初始兴趣偏好
 * 2. 用户注册或首次使用时设置兴趣偏好
 * 3. 为用户批量设置多个兴趣标签
 */
@Data
public class ModelVO {

    private Long userId;
    // 兴趣视频分类
    private List<String> labels;
}