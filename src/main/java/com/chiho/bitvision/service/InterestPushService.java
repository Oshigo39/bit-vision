package com.chiho.bitvision.service;

import com.chiho.bitvision.entity.video.Video;

/**
 * 兴趣推送
 */
public interface InterestPushService {

    /**
     * 添加分类库,用于后续随机推送分类视频
     * @param video video
     */
    void pushSystemTypeStockIn(Video video);

    /**
     * 推入标签库
     * 传videoId,typeId
     * @param video video
     */
    void pushSystemStockIn(Video video);

    /**
     * 删除标签内视频
     * @param video video
     */
    void deleteSystemStockIn(Video video);

    /**
     * 删除分类库中的视频
     * @param video video
     */
    void deleteSystemTypeStockIn(Video video);
}
