package com.chiho.bitvision.entity.video;

import com.chiho.bitvision.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 视频点赞表实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class VideoStar extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long videoId;

    private Long userId;
}