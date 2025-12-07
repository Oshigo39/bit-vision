package com.chiho.bitvision.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chiho.bitvision.entity.user.Favorites;

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
}
