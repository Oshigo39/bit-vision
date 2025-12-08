package com.chiho.bitvision.controller;

import com.chiho.bitvision.entity.vo.BasePage;
import com.chiho.bitvision.service.video.VideoService;
import com.chiho.bitvision.util.JwtUtils;
import com.chiho.bitvision.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/luckyjourney/index")
public class IndexController {

    @Autowired
    private VideoService videoService;


    /**
     * 根据用户id获取视频
     * @param userId id
     * @param basePage page
     * @return user video data by userId
     */
    @GetMapping("/video/user")
    public R listVideoByUserId(@RequestParam(required = false) Long userId,
                               BasePage basePage,
                               HttpServletRequest request){
        userId = userId == null ? JwtUtils.getUserId(request) : userId;
        return R.ok().data(videoService.listByUserIdOpenVideo(userId,basePage));

    }
}
