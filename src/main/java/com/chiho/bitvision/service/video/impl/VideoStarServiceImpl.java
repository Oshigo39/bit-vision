package com.chiho.bitvision.service.video.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chiho.bitvision.entity.video.VideoStar;
import com.chiho.bitvision.mapper.video.VideoStarMapper;
import com.chiho.bitvision.service.video.VideoStarService;
import org.springframework.stereotype.Service;

/**
 * 视频点赞服务实现
 */
@Service
public class VideoStarServiceImpl extends ServiceImpl<VideoStarMapper, VideoStar> implements VideoStarService {

    // 获取视频点赞状态
    @Override
    public Boolean starState(Long videoId, Long userId) {
        if (userId == null) return false;
        // eq()当前videoID的视频记录，然后eq()前面的记录中当前的用户，为一则点赞了
        return this.count(new LambdaQueryWrapper<VideoStar>()
                .eq(VideoStar::getVideoId, videoId)
                .eq(VideoStar::getUserId,userId)) == 1;
    }
}
