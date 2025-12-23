package com.chiho.bitvision.entity.vo;

import lombok.Data;

/**
 * 角色分配VO类，用于用户分配角色的请求参数
 */
@Data
public class AssignRoleVO {
    private Long uId;
    private Long[] rId;
}
