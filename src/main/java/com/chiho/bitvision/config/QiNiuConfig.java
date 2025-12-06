package com.chiho.bitvision.config;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 七牛云各类配置
 * 以及操作资源所需要的token
 */
@Data
@Component
@ConfigurationProperties(prefix = "qiniu.kodo")
public class QiNiuConfig {
    // CDN域名
    public static final String CNAME = "http://t6fpio9yz.hn-bkt.clouddn.com";
    // 视频审核API地址（两者都需要收费）
    public static final String VIDEO_URL = "http://ai.qiniuapi.com/v3/video/censor";
    // 图片审核API地址
    public static final String IMAGE_URL = "http://ai.qiniuapi.com/v3/image/censor";
    // 视频转码参数（指定将视频转码成标准mp4格式后存储）
    public static final String fops = "avthumb/mp4";
    // 账号密钥
    private String accessKey;
    private String secretKey;
    // 存储空间名称
    private String bucketName;

    // 构建七牛云依赖的认证对象
    public Auth buildAuth() {
        return Auth.create(
                this.getAccessKey(), this.getSecretKey()
        );
    }

    // 生成通用上传token
    public String uploadToken(String type) {
        final Auth auth = buildAuth();
        return auth.uploadToken(
                bucketName, null, 300,
                new StringMap()
                        .put("mimeLimit", "video/*;image/*")    // 限制上传视频或图片
        );
    }

    // 生成API请求的token
    public String getToken(String url, String method, String body, String contentType) {
        final Auth auth = buildAuth();
        return "Qiniu " + auth.signQiniuAuthorization(
                url, method, body == null ? null :
                        body.getBytes(), contentType
        );
    }

    // 视频上传token
    public String videoUploadToken() {
        final Auth auth = buildAuth();
        return auth.uploadToken(bucketName, null, 300,
                new StringMap()
                        .put("mimeLimit", "video/*")    // 限制上传视频
                        .putNotEmpty("persistentOps", fops)
        );
    }

    // 图片上传token
    public String imageUploadToken() {
        final Auth auth = buildAuth();
        return auth.uploadToken(
                bucketName, null, 300,
                new StringMap()
                        .put("mimeLimit", "image/*")
        );
    }
}
