package com.chiho.bitvision.entity.vo;

import lombok.Data;

/**
 * 单个兴趣标签模型
 * 对该标签的偏好程度
 */
@Data
public class Model {
    // 兴趣标签
    private String label;
    // 视频ID
    private Long videoId;
    // 兴趣分数（权重）
    private Double score;
}