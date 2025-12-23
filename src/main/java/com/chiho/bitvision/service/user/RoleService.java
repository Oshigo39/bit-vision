package com.chiho.bitvision.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chiho.bitvision.entity.user.Role;
import com.chiho.bitvision.entity.user.Tree;
import com.chiho.bitvision.entity.vo.AssignRoleVO;
import com.chiho.bitvision.entity.vo.AuthorityVO;
import com.chiho.bitvision.util.R;

import java.util.List;

public interface RoleService extends IService<Role> {

    List<Tree> tree();

    R removeRole(String id);

    R gavePermission(AuthorityVO authorityVO);

    R gaveRole(AssignRoleVO assignRoleVO);

}
