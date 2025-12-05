package com.chiho.bitvision.entity.json;

import lombok.Data;

/**
 * 简化版的审核详情映射类
 * 减少不必要的数据传输和处理开销
 */
@Data
public class DetailsChild {
    String score;
    String suggestion;
    String label;
}
