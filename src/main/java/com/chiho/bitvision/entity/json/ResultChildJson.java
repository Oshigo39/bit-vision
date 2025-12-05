package com.chiho.bitvision.entity.json;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 审核结果的子结构映射类
 */
@Data
@ToString
public class ResultChildJson implements Serializable {
    String suggestion;
    ScenesJson scenes;
}
