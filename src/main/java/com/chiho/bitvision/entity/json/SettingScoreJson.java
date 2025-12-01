package com.chiho.bitvision.entity.json;

import lombok.Data;

/**
 * 系统表解析
 */
@Data
public class SettingScoreJson {
    // 通过
    ScoreJson successScore;
    // 人工审核
    ScoreJson manualScore;
    // PASS
    ScoreJson passScore;
}
