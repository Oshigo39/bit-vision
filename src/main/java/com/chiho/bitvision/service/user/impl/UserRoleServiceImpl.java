package com.chiho.bitvision.service.user.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chiho.bitvision.entity.user.UserRole;
import com.chiho.bitvision.mapper.user.UserRoleMapper;
import com.chiho.bitvision.service.user.UserRoleService;
import org.springframework.stereotype.Service;

@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

}
