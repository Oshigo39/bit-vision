package com.chiho.bitvision.service.user.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chiho.bitvision.entity.user.UserSubscribe;
import com.chiho.bitvision.mapper.user.UserSubscribeMapper;
import com.chiho.bitvision.service.user.UserSubscribeService;
import org.springframework.stereotype.Service;

@Service
public class UserSubscribeServiceImpl extends ServiceImpl<UserSubscribeMapper, UserSubscribe> implements UserSubscribeService {
}
