package com.chiho.bitvision.entity.json;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 视频帧截图审核结果的映射类
 */
@Data
@ToString
public class CutsJson implements Serializable {
    // 截图的审核详情
    List<DetailsJson> details;
    // 审核建议
    String suggestion;
    // 视频中的时间偏移量
    Long offset;
}