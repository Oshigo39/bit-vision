package com.chiho.bitvision.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chiho.bitvision.entity.File;


/**
 * 文件元数据管理接口
 */
public interface FileService extends IService<File> {

    /**
     * 将已上传到七牛云的文件信息保存到本地数据库
     * @param fileKey k
     * @param userId id
     * @return long
     */
    Long save(String fileKey, Long userId);

    /**
     * 七牛云截取帧api生成
     * 根据视频ID生成图片
     * @param fileId F_ID
     * @param userId U_ID
     * @return Long
     */
    Long generatePhoto(Long fileId, Long userId);

    /**
     * 获取文件真实URL
     * @param fileId F_ID
     * @return file info
     */
    File getFileTrustUrl(Long fileId);
}
