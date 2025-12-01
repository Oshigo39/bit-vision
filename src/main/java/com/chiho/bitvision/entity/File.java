package com.chiho.bitvision.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 文件元数据实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class File extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    // 文件key
    private String fileKey;

    // 文件格式
    private String format;

    // 文件类型
    private String type;

    // 文件大小
    private Long size;

    // 发布者
    private Long userId;
}
