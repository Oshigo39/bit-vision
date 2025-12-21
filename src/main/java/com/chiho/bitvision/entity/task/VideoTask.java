package com.chiho.bitvision.entity.task;

import com.chiho.bitvision.entity.video.Video;
import lombok.Data;

/**
 * 用于封装视频发布相关的任务信息
 * 本质上是一个DTO
 * 主要应用于视频发布审核流程
 */
@Data
public class VideoTask {

    // 新视频
    private Video video;

    // 老视频
    private Video oldVideo;

    // 是否是新增
    private Boolean isAdd;

    // 老状态 : 0 公开  1 私密
    private Boolean oldState;

    // 新状态
    private Boolean newState;
}
