package com.chiho.bitvision.entity.json;

import lombok.Data;

/**
 * 具体审核配型的分数配置映射
 */
@Data
public class ScoreJson{
    // 最大最小色情内容
    Double minPulp;
    Double maxPulp;

    // 恐怖
    Double minTerror;
    Double maxTerror;

    // 涉证
    Double minPolitician;
    Double maxPolitician;

    // 状态
    Integer auditStatus;

}
