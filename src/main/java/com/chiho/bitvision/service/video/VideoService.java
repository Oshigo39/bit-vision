package com.chiho.bitvision.service.video;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chiho.bitvision.entity.video.Video;
import com.chiho.bitvision.entity.vo.BasePage;

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
}
