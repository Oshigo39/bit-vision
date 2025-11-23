package com.chiho.bitvision.service.user.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chiho.bitvision.entity.user.Favorites;
import com.chiho.bitvision.mapper.user.FavoritesMapper;
import com.chiho.bitvision.service.user.FavoritesService;
import org.springframework.stereotype.Service;

/**
 * FavoritesService 的 实现类
 */
@Service
public class FavoritesServiceImpl extends ServiceImpl<FavoritesMapper, Favorites> implements FavoritesService {

}
