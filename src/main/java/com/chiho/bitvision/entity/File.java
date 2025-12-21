package com.chiho.bitvision.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 文件元数据实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class File extends BaseEntity implements Serializable{

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 文件key
    private String fileKey;

    // 文件格式
    private String format;

    // 文件类型
    private String type;

    // 视频时长
    private String duration;

    // 文件大小
    private Long size;

    // 发布者
    private Long userId;
}
