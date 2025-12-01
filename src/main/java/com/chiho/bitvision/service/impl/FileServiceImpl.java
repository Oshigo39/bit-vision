package com.chiho.bitvision.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chiho.bitvision.config.LocalCache;
import com.chiho.bitvision.config.QiNiuConfig;
import com.chiho.bitvision.entity.File;
import com.chiho.bitvision.exception.BaseException;
import com.chiho.bitvision.mapper.FileMapper;
import com.chiho.bitvision.service.FileService;
import com.chiho.bitvision.service.QiNiuFileService;
import com.qiniu.storage.model.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

/**
 * 文件元数据接口实现
 * todo 编写实现逻辑
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

    @Autowired
    private QiNiuFileService qiNiuFileService;

    // 将已上传到七牛云的文件信息保存到本地数据库
    @Override
    public Long save(String fileKey, Long userId) {
        // 检查文件是否存在于七牛云数据库当中
        final FileInfo uploadedFileInfo = qiNiuFileService.getFileInfo(fileKey);
        if (uploadedFileInfo == null){
            throw new IllegalArgumentException("参数不正确");
        }
        final File uploadedFile = new File();
        // 获取文件类型
        String type = uploadedFileInfo.mimeType;
        uploadedFile.setFileKey(fileKey);
        uploadedFile.setFormat(type);
        uploadedFile.setType(type.contains("video") ? "视频" : "图片");
        uploadedFile.setUserId(userId);
        uploadedFile.setSize(uploadedFileInfo.fsize);
        save(uploadedFile);
        // 返回已上传的文件的元数据的字段ID
        return uploadedFile.getId();
    }

    // 为已存在的视频文件生成封面图片记录
    @Override
    public Long generatePhoto(Long fileId, Long userId) {
        final File file = getById(fileId);
        // 七牛云存储的视频帧截取功能 ： ?vframe/jpg/offset/1
        // 数字是帧位置
        final String fileKey = file.getFileKey() + "?vframe/jpg/offset/1";
        final File fileInfo = new File();
        fileInfo.setFileKey(fileKey);
        fileInfo.setFormat("image/*");
        fileInfo.setType("图片");
        fileInfo.setUserId(userId);
        save(fileInfo);
        return fileInfo.getId();
    }

    @Override
    public File getFileTrustUrl(Long fileId) {
        File file = getById(fileId);
        if (Objects.isNull(file)) {
            throw new BaseException("未找到该文件");
        }
        final String s = UUID.randomUUID().toString();
        LocalCache.put(s,true);
        String url = QiNiuConfig.CNAME + "/" + file.getFileKey();

        if (url.contains("?")){
            url = url+"&uuid="+s;
        }else {
            url = url+"?uuid="+s;
        }
        file.setFileKey(url);
        return file;
    }
}
