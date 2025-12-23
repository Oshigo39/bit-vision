package com.chiho.bitvision.entity.vo;

import lombok.Data;

/**
 * 权限分配VO类，用于角色分配权限的请求参数
 */
@Data
public class AuthorityVO {

    private Integer rid;

    private Integer[] pid;
}
