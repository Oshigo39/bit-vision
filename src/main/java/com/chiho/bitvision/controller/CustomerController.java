package com.chiho.bitvision.controller;

import com.chiho.bitvision.config.QiNiuConfig;
import com.chiho.bitvision.entity.vo.BasePage;
import com.chiho.bitvision.entity.vo.UpdateUserVO;
import com.chiho.bitvision.holder.UserHolder;
import com.chiho.bitvision.service.user.FavoritesService;
import com.chiho.bitvision.service.user.UserService;
import com.chiho.bitvision.util.R;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户个人信息控制器
 */
@Slf4j
@RestController
@RequestMapping("/luckyjourney/customer")
public class CustomerController {

    @Autowired
    private QiNiuConfig qiNiuConfig;

    @Autowired
    private UserService userService;

    @Autowired
    private FavoritesService favoritesService;

    /**
     * 获取指定用户的个人信息
     * todo 或许应该移到IndexController当中，让没有登录也能在主页看到信息
     * @param userId userid
     * @return data
     */
    @GetMapping("/getInfo/{userInfo}")
    public R getInfo(@PathVariable Long userId){
        return R.ok().data(userService.getInfo(userId));

    }

    /**
     * 获取当前登录用户的个人信息
     * @return data
     */
    @GetMapping("/getInfo")
    public R getDefaultInfo(){
        return R.ok().data(userService.getInfo(UserHolder.get()));
    }

    /**
     * 修改用户个人信息
     * @param userVO update_vo
     * @return ? msg
     */
    @PutMapping
    public R updateUser(@RequestBody @Validated UpdateUserVO userVO){
        // 获取当前的userId
        userVO.setUserId(UserHolder.get());
        userService.updateUser(userVO);
        return R.ok().message("修改成功");
    }

    /**
     * 获取用户上传头像的token，提供给前端进行文件上传
     * @return token
     */
    @GetMapping("/avatar/token")
    public R avatarToken(){
        return R.ok().data(qiNiuConfig.imageUploadToken());
    }

    /**
     * 关注/取关
     * @param followsUserId 前端传进来的目标ID
     * @return msg
     */
    @PostMapping("/follows")
    public R follows(@RequestParam Long followsUserId){
        Long userId = UserHolder.get();
        return R.ok().message(userService.follows(followsUserId,userId) ? "已关注" : "已取关");
    }

    /**
     * 获取关注者
     * @param basePage page分页
     * @param userId userId
     * @return data
     */
    @GetMapping("/follows")
    public R getFollows(BasePage basePage, Long userId){
        return R.ok().data(userService.getFollows(basePage,userId));
    }

    /**
     * 获取粉丝
     * @param basePage page
     * @param userId userId
     * @return data
     */
    @GetMapping("/fans")
    public R getFans(BasePage basePage, Long userId){
        return R.ok().data(userService.getFans(userId,basePage));
    }
}
