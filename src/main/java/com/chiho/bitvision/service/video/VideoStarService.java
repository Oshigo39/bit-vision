package com.chiho.bitvision.service.video;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chiho.bitvision.entity.video.VideoStar;

public interface VideoStarService extends IService<VideoStar> {

    /**
     * 点赞状态
     * @param videoId videoID
     * @param userId userID
     * @return ?
     */
    Boolean starState(Long videoId, Long userId);
}
