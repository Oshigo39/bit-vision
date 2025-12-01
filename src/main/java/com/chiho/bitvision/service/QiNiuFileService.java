package com.chiho.bitvision.service;

import com.qiniu.storage.model.FileInfo;

import java.io.File;

/**
 * 七牛云的文件操作接口（继承于基础的FileCloudService）
 */
public interface QiNiuFileService extends FileCloudService{
    /**
     * 获取上传签名
     */
    String getToken();

    /**
     * 文件上传接口
     * @param file 上传的文件
     * @return 串
     */
    String uploadFile(File file);

    /**
     * 删除文件
     * @param url 需要删除的文件的url
     */
    void deleteFile(String url);

    /**
     * 获取文件信息
     * @param url url
     * @return 文件信息
     */
    FileInfo getFileInfo(String url);
}
