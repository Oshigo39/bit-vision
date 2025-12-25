package com.chiho.bitvision.service.video;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chiho.bitvision.entity.video.Video;
import com.chiho.bitvision.entity.vo.BasePage;

import java.util.Collection;

public interface VideoService extends IService<Video> {

    /**
     * 根据userId获取对应视频,只包含公开的
     * @param userId user's id
     * @param basePage base page entity
     * @return IPage with MP
     */
    IPage<Video> listByUserIdOpenVideo(Long userId, BasePage basePage);

    /**
     * 收藏视频
     * @param fId 收藏夹ID
     * @param vId 视频ID
     * @return ?
     */
    boolean favoritesVideo(Long fId, Long vId);

    /**
     * 根据收藏夹获取视频
     * @param favoritesId 收藏夹ID
     * @return 收藏夹下的视频
     */
    Collection<Video> listVideoByFavorites(Long favoritesId);

    /**
     * 发布/修改视频接口
     * 修改无法更改视频源
     * @param video video
     */
    void publishVideo(Video video);

    /**
     * 获取视频信息
     * @param id 视频id
     * @param userId 当前用户id
     * @return data of video
     */
    Video getVideoById(Long id,Long userId);
}
