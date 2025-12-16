package com.chiho.bitvision.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chiho.bitvision.entity.user.Favorites;
import com.chiho.bitvision.entity.user.FavoritesVideo;
import com.chiho.bitvision.exception.BaseException;
import com.chiho.bitvision.holder.UserHolder;
import com.chiho.bitvision.mapper.user.FavoritesMapper;
import com.chiho.bitvision.service.user.FavoritesService;
import com.chiho.bitvision.service.user.FavoritesVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * FavoritesService 的 实现类
 */
@Service
public class FavoritesServiceImpl extends ServiceImpl<FavoritesMapper, Favorites> implements FavoritesService {

    @Autowired
    private FavoritesVideoService favoritesVideoService;

    // 校验用户的默认收藏夹是否存在
    @Override
    public void exist(Long userId, Long fId) {
        // 先在表中查找所有符合user_id的字段（用户创建收藏夹就会创建一条记录）
        // 然后在上面的基础上在比对默认收藏夹是否存在
        count(new LambdaQueryWrapper<Favorites>().eq(Favorites::getUserId,userId).eq(Favorites::getId,fId));
        if (count() == 0){
            throw new BaseException("收藏夹选择错误");
        }
    }

    // 删除收藏夹与关联的视频数据，Transactional保证操作完整性，否则回滚
    @Transactional
    @Override
    public void remove(Long id, Long userId) {
        // 不能删除默认收藏夹
        final Favorites favorites = getOne(new LambdaQueryWrapper<Favorites>()
                .eq(Favorites::getId, id)
                .eq(Favorites::getUserId, userId));
        if (favorites.getName().equals("默认收藏夹"))
            throw new BaseException("不允许删除默认收藏夹");
        final boolean result = remove(new LambdaQueryWrapper<Favorites>()
                .eq(Favorites::getId, id)
                .eq(Favorites::getUserId, userId));
        if (result)
            favoritesVideoService.remove(new LambdaQueryWrapper<FavoritesVideo>()
                    .eq(FavoritesVideo::getFavoritesId, id));
        else throw new BaseException("你小子还想删别人的收藏夹?");
    }

    @Override
    public List<Favorites> listByIds(Long userId) {
        // 查询用户收藏夹列表
        final List<Favorites> favorites = list(new LambdaQueryWrapper<Favorites>()
                .eq(Favorites::getUserId, userId));
        if (ObjectUtils.isEmpty(favorites)) return Collections.EMPTY_LIST;  // 空值处理
        // 提取所有收藏夹的ID，形成一个ID列表
        final List<Long> fIds = favorites
                .stream()
                .map(Favorites::getId)
                .collect(Collectors.toList());
        // 统计各收藏夹视频数量
        final Map<Long, Long> fMap = favoritesVideoService.list(
                new LambdaQueryWrapper<FavoritesVideo>()
                        .in(FavoritesVideo::getFavoritesId, fIds))
                .stream().collect(Collectors.groupingBy(FavoritesVideo::getFavoritesId, Collectors.counting()));
        // 设置收藏夹视频数量
        for (Favorites favorite : favorites) {
            final Long videoCount = fMap.get(favorite.getId());
            favorite.setVideoCount(videoCount == null ? 0 :videoCount);
        }
        // 返回包含视频数量信息的收藏夹列表
        return favorites;
    }

    // 收藏视频
    @Override
    public boolean favorites(Long fId, Long vId) {
        final Long userId = UserHolder.get();
        try {
            final FavoritesVideo favoritesVideo = new FavoritesVideo();
            favoritesVideo.setFavoritesId(fId);
            favoritesVideo.setVideoId(vId);
            favoritesVideo.setUserId(userId);
            favoritesVideoService.save(favoritesVideo);
        }catch (Exception e) {
            favoritesVideoService.remove(new LambdaQueryWrapper<FavoritesVideo>()
                    .eq(FavoritesVideo::getFavoritesId, fId)
                    .eq(FavoritesVideo::getVideoId,vId)
                    .eq(FavoritesVideo::getUserId, userId));
            return false;
        }
        return true;
    }
}
