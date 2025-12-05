package com.chiho.bitvision.entity.json;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 审核场景集合的映射类
 */
@Data
@ToString
public class ScenesJson implements Serializable {
    // 恐怖内容
    private TypeJson terror;
    // 政治人物
    private TypeJson politician;
    // 色情内容
    private TypeJson pulp;
    // 垃圾内容
    private TypeJson antispam;
}
