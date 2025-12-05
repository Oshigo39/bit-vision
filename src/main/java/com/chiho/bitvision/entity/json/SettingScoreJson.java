package com.chiho.bitvision.entity.json;

import lombok.Data;

/**
 * 系统审核策略配置的映射类
 * 实现审核规则的可配置化，无需修改代码即可调节审核策略
 */
@Data
public class SettingScoreJson {
    // 自动通过的分数阈值
    ScoreJson successScore;
    // 需要人工审核的分数阈值
    ScoreJson manualScore;
    // 直接跳过的分数阈值
    ScoreJson passScore;
}
