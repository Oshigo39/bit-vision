package com.chiho.bitvision.controller;

import com.chiho.bitvision.service.QiNiuFileService;
import com.chiho.bitvision.service.video.VideoService;
import com.chiho.bitvision.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
