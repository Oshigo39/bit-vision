package com.chiho.bitvision.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chiho.bitvision.entity.user.Favorites;

import java.util.List;

/**
 * Favorites 的 Service 接口
 */
public interface FavoritesService extends IService<Favorites> {

    /**
     * 校验用户的默认收藏夹是否存在
     * @param userId 用户id
     * @param defaultFavoritesId 默认收藏夹ID
     */
    void exist(Long userId, Long defaultFavoritesId);

    /**
     * 删除收夹，并将关联的视频数据一并清除
     * @param id favorites id
     * @param userId current user id
     */
    void remove(Long id, Long userId);

    /**
     * 获取当前用户所有的收藏夹
     * @param userId userId
     * @return list by user's favorites
     */
    List<Favorites> listByIds(Long userId);

    /**
     * 收藏视频
     * @param fId 收藏夹ID
     * @param vId 视频ID
     */
    boolean favorites(Long fId, Long vId);

    /**
     * 获取收藏夹下的所有视频的ID
     * @param favoritesId 收藏夹ID
     * @param userId userId
     * @return 视频ID列表
     */
    List<Long> listVideoIds(Long favoritesId, Long userId);
}
