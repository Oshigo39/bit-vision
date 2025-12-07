package com.chiho.bitvision.service.user.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chiho.bitvision.entity.user.Follow;
import com.chiho.bitvision.mapper.FollowMapper;
import com.chiho.bitvision.service.user.FollowService;
import org.springframework.stereotype.Service;

@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService {

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
}
