package com.chiho.bitvision.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chiho.bitvision.entity.video.Type;
import com.chiho.bitvision.entity.vo.BasePage;
import com.chiho.bitvision.service.user.UserService;
import com.chiho.bitvision.service.video.TypeService;
import com.chiho.bitvision.service.video.VideoService;
import com.chiho.bitvision.util.JwtUtils;
import com.chiho.bitvision.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/luckyjourney/index")
public class IndexController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private UserService userService;


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

    @GetMapping("/types")
    public R getTypes(HttpServletRequest request) {
        // 获取所有分类
        final List<Type> types = typeService.list(
                new LambdaQueryWrapper<Type>()
                .select(Type::getIcon, Type::getId, Type::getName)
                .orderByDesc(Type::getSort)
        );
        // 标记用户已订阅的分类（Set自动去除重复元素，高效判断元素是否存在）
        final Set<Long> set = userService
                .listSubscribeType(JwtUtils.getUserId(request))
                .stream().map(Type::getId).collect(Collectors.toSet());
        for (Type type : types) {
            type.setUsed(set.contains(type.getId()));   // 判断元素是否存在
        }
        return R.ok().data(types);
    }

    /**
     * 根据id获取视频详情
     * @param id id
     * @return data of video
     */
    @GetMapping("/video/{id}")
    public R getVideoById(@PathVariable Long id, HttpServletRequest request){
        final Long userId = JwtUtils.getUserId(request);
        return R.ok().data(videoService.getVideoById(id,userId));
    }
}
