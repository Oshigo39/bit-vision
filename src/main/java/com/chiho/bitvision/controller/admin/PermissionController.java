package com.chiho.bitvision.controller.admin;

import com.chiho.bitvision.authority.Authority;
import com.chiho.bitvision.entity.user.Permission;
import com.chiho.bitvision.holder.UserHolder;
import com.chiho.bitvision.service.user.PermissionService;
import com.chiho.bitvision.util.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/authorize/permission")
public class PermissionController {

    @Resource
    private PermissionService permissionService;

    /**
     * 权限列表
     */
    @GetMapping("/list")
    @Authority("permission:list")
    public List<Permission> list(){
        return permissionService.list(null);
    }


    /**
     * 新增权限时树形结构
     */
    @GetMapping("/treeSelect")
    @Authority("permission:treeSelect")
    public List<Permission> treeSelect(){

        return permissionService.treeSelect();
    }

    /**
     * 添加权限
     */
    @PostMapping
    @Authority("permission:add")
    public R add(@RequestBody Permission permission){
        permission.setIcon("fa "+permission.getIcon());
        permissionService.save(permission);

        return R.ok();
    }

    /**
     * 修改权限
     */
    @PutMapping
    @Authority("permission:update")
    public R update(@RequestBody Permission permission){
        permission.setIcon("fa "+permission.getIcon());
        permissionService.updateById(permission);
        return R.ok();

    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    @Authority("permission:delete")
    public R delete(@PathVariable Long id){
        permissionService.removeMenu(id);
        return R.ok().message("删除成功");
    }


    /**
     * 初始化菜单
     */
    @GetMapping("/initMenu")
    public Map<String, Object> initMenu(){
        return permissionService.initMenu(UserHolder.get());
    }
}


