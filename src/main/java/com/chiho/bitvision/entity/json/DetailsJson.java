package com.chiho.bitvision.entity.json;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 审核详情信息的映射类
 */
@Data
@ToString
public class DetailsJson implements Serializable {
    // 审核分数
    Double score;
    // 审核建议
    String suggestion;
    // 违规标签
    String label;
    // 违规分组
    String group;
}