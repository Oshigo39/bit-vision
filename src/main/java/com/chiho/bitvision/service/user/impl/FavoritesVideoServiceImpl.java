package com.chiho.bitvision.service.user.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chiho.bitvision.entity.user.FavoritesVideo;
import com.chiho.bitvision.mapper.user.FavoritesVideoMapper;
import com.chiho.bitvision.service.user.FavoritesVideoService;
import org.springframework.stereotype.Service;

@Service
public class FavoritesVideoServiceImpl extends ServiceImpl<FavoritesVideoMapper, FavoritesVideo> implements FavoritesVideoService {
}
