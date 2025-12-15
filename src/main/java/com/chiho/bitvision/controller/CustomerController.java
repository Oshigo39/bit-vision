package com.chiho.bitvision.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chiho.bitvision.config.QiNiuConfig;
import com.chiho.bitvision.entity.user.Favorites;
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

import java.util.List;

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

    /**
     * 添加/修改收藏夹
     * @param favorites 收藏夹实体类
     * @return msg
     */
    @PostMapping("/favorites")
    public R saveOrUpdateFavorites(@RequestBody @Validated Favorites favorites){
        final Long userId = UserHolder.get();
        final Long id = favorites.getId();  // 收藏夹ID
        favorites.setUserId(userId);
        // 判断收藏夹是否已经存在
        final int count = (int) favoritesService.count(
                new LambdaQueryWrapper<Favorites>()
                .eq(Favorites::getName, favorites.getName())
                .eq(Favorites::getUserId, userId)
                .ne(Favorites::getId, favorites.getId()));
        if (count == 1)
            return R.error().message("已存在相同名称的收藏夹");
        favoritesService.saveOrUpdate(favorites);
        return R.ok().message(id !=null ? "修改成功" : "添加成功");
    }

    /**
     * 获取指定收藏夹
     * @param id 收藏夹id
     * @return favorite by id
     */
    @GetMapping("/favorites/{id}")
    public R getFavorites(@PathVariable Long id) {
        return R.ok().data(favoritesService.getById(id));
    }

    /**
     * 删除收藏夹
     * @param id 要删除的收藏夹ID
     * @return msg
     */
    @DeleteMapping("/favorites/{id}")
    public R deleteFavorites(@PathVariable Long id) {
        favoritesService.remove(id,UserHolder.get());
        return R.ok().message("删除成功");
    }

    /**
     * 获取所有的收藏夹
     * @return data
     */
    @GetMapping("/favorites")
    public R listFavorites(){
        final Long userId = UserHolder.get();
        List<Favorites> favorites = favoritesService.listByIds(userId);
        return R.ok().data(favorites);
    }
}
