package com.chiho.bitvision.service.impl;

import com.chiho.bitvision.config.QiNiuConfig;
import com.chiho.bitvision.service.QiNiuFileService;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * 七牛云的文件操作的实现
 * - UploadManager进行文件上传
 * - BucketManager 进行文件删除和信息查询
 * - Auth 进行身份验证
 * - Gson 解析上传结果
 */
@Service
public class QiNiuFileServiceImpl implements QiNiuFileService {
    private static final Logger log = LoggerFactory.getLogger(QiNiuFileServiceImpl.class);
    // 获取七牛云的配置与token
    @Autowired
    QiNiuConfig qiNiuConfig;

    @Override
    public String getToken() {
        return qiNiuConfig.videoUploadToken();
    }

    @Override
    public String uploadFile(File file) {
        Configuration cfg = new Configuration(Region.region2());
        UploadManager uploadManager = new UploadManager(cfg);
        try {
            // 进行上传
            Response response = uploadManager
                    .put(file, null, qiNiuConfig.videoUploadToken());
            // 解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            return putRet.key;
        } catch (QiniuException e) {
            log.error("文件上传失败：{}", e.getMessage());
            if (e.response != null) {
                System.err.println(e.response);

                try {
                    String body = e.response.toString();
                    System.err.println(body);
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }

    @Override
    @Async
    public void deleteFile(String url) {
        Configuration cfg = new Configuration(Region.region0());
        String bucket = qiNiuConfig.getBucketName();
        final Auth auth = qiNiuConfig.buildAuth();
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            // 删除操作
            bucketManager.delete(bucket, url);
        } catch (QiniuException e) {
            //如果遇到异常，说明删除失败
            System.err.println(e.code());
            System.err.println(e.response.toString());
        }
    }

    @Override
    public FileInfo getFileInfo(String url) {
        Configuration cfg = new Configuration(Region.region0());
        final Auth auth = qiNiuConfig.buildAuth();
        final String bucket = qiNiuConfig.getBucketName();
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            // 获取并返回文件信息
            return bucketManager.stat(bucket, url);
        } catch (QiniuException e) {
            System.err.println(e.response.toString());
        }
        return null;
    }
}
