package com.chiho.bitvision.service;

import java.util.Collection;

public interface FeedService {
    /**
     * 推入发件箱
     * @param userId 发件箱用户id
     * @param videoId 视频id
     */
    void pusOutBoxFeed(Long userId,Long videoId,Long time);

    /**
     * 删除发件箱
     * 当前用户删除视频时 调用
     * ->删除当前用户的发件箱中视频以及粉丝下的收件箱
     * @param userId 当前用户
     * @param fans 粉丝id
     * @param videoId 视频id 需要删除的
     */
    void deleteOutBoxFeed(Long userId,Collection<Long> fans,Long videoId);
}
