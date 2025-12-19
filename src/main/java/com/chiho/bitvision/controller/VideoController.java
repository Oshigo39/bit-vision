package com.chiho.bitvision.controller;

import com.chiho.bitvision.entity.video.Video;
import com.chiho.bitvision.limit.Limit;
import com.chiho.bitvision.service.QiNiuFileService;
import com.chiho.bitvision.service.video.VideoService;
import com.chiho.bitvision.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/luckyjourney/video")
public class VideoController {

    @Autowired
    private QiNiuFileService fileService;

    @Autowired
    private VideoService videoService;

    /**
     * 收藏视频
     * @param fId 收藏夹ID
     * @param vId 视频ID
     * @return msg
     */
    @PostMapping("/favorites/{fId}/{vId}")
    public R favoritesVideo(@PathVariable Long fId,@PathVariable Long vId) {
        String msg = videoService.favoritesVideo(fId,vId) ? "已收藏" : "取消收藏";
        return R.ok().message(msg);
    }

    /**
     * 获取收藏夹下的视频
     * @param favoritesId 收藏夹名称
     * @return data
     */
    @GetMapping("/favorites/{favoritesId}")
    public R listVideoByFavorites(@PathVariable Long favoritesId) {
        return R.ok().data(videoService.listVideoByFavorites(favoritesId));
    }

    /**
     * 发布/修改视频
     * @param video 视频元数据对象
     * @return msg
     */
    @PostMapping
    @Limit(limit = 5,time = 3600L,msg = "发布视频一小时内不可超过5次")
    public R publishVideo(@RequestBody @Validated Video video){
        videoService.publishVideo(video);
        return R.ok().message("发布成功，请等待审核");
    }

}
