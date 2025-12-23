package com.chiho.bitvision.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chiho.bitvision.authority.Authority;
import com.chiho.bitvision.entity.user.Role;
import com.chiho.bitvision.entity.user.RolePermission;
import com.chiho.bitvision.entity.user.Tree;
import com.chiho.bitvision.entity.user.UserRole;
import com.chiho.bitvision.entity.vo.AssignRoleVO;
import com.chiho.bitvision.entity.vo.AuthorityVO;
import com.chiho.bitvision.entity.vo.BasePage;
import com.chiho.bitvision.service.user.RolePermissionService;
import com.chiho.bitvision.service.user.RoleService;
import com.chiho.bitvision.service.user.UserRoleService;
import com.chiho.bitvision.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/authorize/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private UserRoleService userRoleService;


    @GetMapping("/treeList")
    @Authority("permission:treeList")
    public List<Tree> treeList(){
        return roleService.tree();
    }

    @PostMapping("/assignRole")
    @Authority("user:assignRole")
    public R assignRole(@RequestBody AssignRoleVO assignRoleVO){

        return roleService.gaveRole(assignRoleVO);
    }


    @GetMapping("/getUserRole/{userId}")
    @Authority("role:getRole")
    public List getRole(@PathVariable Integer userId){
        return userRoleService.list(new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId,userId)
                        .select(UserRole::getRoleId))
                .stream().map(UserRole::getRoleId).collect(Collectors.toList());
    }

    /**
     * 初始化角色
     */
    @GetMapping("/initRole")
    @Authority("role:initRole")
    public List<Map<String, Object>> initRole(){
        // 查出所有角色
        return roleService.list(null).stream()
                .map(role -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("value", role.getId());
                    data.put("title", role.getName());
                    return data;
                }).collect(Collectors.toList());
    }


    @GetMapping("/list")
    @Authority("role:list")
    public R list(BasePage basePage, @RequestParam(required = false) String name){
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(!ObjectUtils.isEmpty(name),Role::getName,name);
        final IPage iPage = basePage.page();
        IPage<Role> page = roleService.page(iPage,wrapper);
        return R.ok().data(page.getRecords()).count(page.getRecords().size());
    }

    /**
     * 添加角色
     */
    @PostMapping
    @Authority("role:add")
    public R add(@RequestBody Role role){
        roleService.save(role);
        return R.ok();
    }

    /**
     * 修改角色
     */
    @PutMapping
    @Authority("role:update")
    public R update(@RequestBody Role role){
        roleService.updateById(role);
        return R.ok();
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @Authority("role:delete")
    public R delete(@PathVariable String id){
        return roleService.removeRole(id);
    }


    /**
     * 给角色分配权限
     * 给角色分配权限前先把该角色的权限都删了
     */
    @PostMapping("/authority")
    @Authority("role:authority")
    public R authority(@RequestBody AuthorityVO authorityVO){
        return roleService.gavePermission(authorityVO);
    }

    /**
     * 获取角色权限
     */
    @GetMapping("/getPermission/{id}")
    @Authority("role:getPermission")
    public Integer[] getPermission(@PathVariable Integer id){
        return rolePermissionService.list(new LambdaQueryWrapper<RolePermission>()
                        .eq(RolePermission::getRoleId,id)
                        .select(RolePermission::getPermissionId))
                .stream().map(RolePermission::getPermissionId).toArray(Integer[]::new);
    }

}


