package com.chiho.bitvision.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chiho.bitvision.entity.user.Favorites;
import com.chiho.bitvision.exception.BaseException;
import com.chiho.bitvision.mapper.user.FavoritesMapper;
import com.chiho.bitvision.service.user.FavoritesService;
import org.springframework.stereotype.Service;

/**
 * FavoritesService 的 实现类
 */
@Service
public class FavoritesServiceImpl extends ServiceImpl<FavoritesMapper, Favorites> implements FavoritesService {

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
}
