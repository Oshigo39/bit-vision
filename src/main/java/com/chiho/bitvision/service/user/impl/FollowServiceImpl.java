package com.chiho.bitvision.service.user.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chiho.bitvision.constant.RedisConstant;
import com.chiho.bitvision.entity.user.Follow;
import com.chiho.bitvision.exception.BaseException;
import com.chiho.bitvision.mapper.FollowMapper;
import com.chiho.bitvision.service.user.FollowService;
import com.chiho.bitvision.util.RedisCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    // 获取当前用户的关注数量
    // 从表中查找并eq所有和当前userId相同的字段并统计
    @Override
    public long getFollowCount(Long userId) {
        return count(new LambdaQueryWrapper<Follow>().eq(Follow::getUserId,userId));
    }

    // 获取当前用户的粉丝数量
    @Override
    public long getFansCount(Long userId) {
        return count(new LambdaQueryWrapper<Follow>().eq(Follow::getFollowId,userId));
    }

    @SuppressWarnings("unchecked")  // 不检查警告
    @Override
    public boolean follows(Long followsUserId, Long userId) {
        if (followsUserId.equals(userId))
            throw new BaseException("不能关注自己");

        // 直接保存(数据库唯一索引，确保一个用户只能关注另一个用户一次)，保存失败则删除
        final Follow follow = new Follow();
        follow.setFollowId(followsUserId);
        follow.setUserId(userId);
        try{
            save(follow);
            Date date = new Date();
            // 自己的关注列表添加
            redisTemplate.opsForZSet().add(
                    RedisConstant.USER_FOLLOW
                            + userId, followsUserId, date.getTime());
            // 对方的粉丝列表添加
            redisTemplate.opsForZSet().add(
                    RedisConstant.USER_FANS
                        + followsUserId, userId, date.getTime());
        }catch (Exception e){   // 检测到存在重复内容时，抛出异常，并来到catch
            // 删除
            remove(new LambdaQueryWrapper<Follow>()
                    .eq(Follow::getFollowId, followsUserId)
                    .eq(Follow::getUserId,userId));
            // 清除关注用户视频推荐收件箱中的视频
            // 获取关注人的视频 todo Feed流内容
            return false;   // 取消关注，返回false
        }
        return true;
    }
}
