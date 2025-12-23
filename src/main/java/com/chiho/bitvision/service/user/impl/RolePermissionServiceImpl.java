package com.chiho.bitvision.service.user.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chiho.bitvision.entity.user.RolePermission;
import com.chiho.bitvision.mapper.user.RolePermissionMapper;
import com.chiho.bitvision.service.user.RolePermissionService;
import org.springframework.stereotype.Service;

@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService {

}
