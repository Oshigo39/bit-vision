package com.chiho.bitvision.service.impl;

import com.chiho.bitvision.constant.RedisConstant;
import com.chiho.bitvision.entity.video.Video;
import com.chiho.bitvision.service.InterestPushService;
import com.chiho.bitvision.service.video.TypeService;
import com.chiho.bitvision.util.RedisCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterestPushServiceImpl implements InterestPushService {

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private TypeService typeService;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    @Async
    public void pushSystemTypeStockIn(Video video) {
        final Long typeId = video.getTypeId();
        redisCacheUtil.sSet(RedisConstant.SYSTEM_TYPE_STOCK + typeId,video.getId());
    }

    @Override
    @Async
    public void pushSystemStockIn(Video video) {
        // 往系统库中添加
        final List<String> labels = video.buildLabel();
        final Long videoId = video.getId();
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (String label : labels) {
                connection.sAdd((RedisConstant.SYSTEM_STOCK + label).getBytes(),String.valueOf(videoId).getBytes());
            }
            return null;
        });
    }

    @Override
    @Async
    public void deleteSystemStockIn(Video video) {
        final List<String> labels = video.buildLabel();
        final Long videoId = video.getId();
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (String label : labels) {
                connection.sRem((RedisConstant.SYSTEM_STOCK + label).getBytes(),String.valueOf(videoId).getBytes());
            }
            return null;
        });
    }

    @Override
    @Async
    public void deleteSystemTypeStockIn(Video video) {
        final Long typeId = video.getTypeId();
        redisCacheUtil.setRemove(RedisConstant.SYSTEM_TYPE_STOCK + typeId,video.getId());
    }
}
