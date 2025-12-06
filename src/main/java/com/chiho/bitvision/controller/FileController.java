package com.chiho.bitvision.controller;

import com.chiho.bitvision.config.LocalCache;
import com.chiho.bitvision.config.QiNiuConfig;
import com.chiho.bitvision.entity.File;
import com.chiho.bitvision.entity.Setting;
import com.chiho.bitvision.holder.UserHolder;
import com.chiho.bitvision.service.FileService;
import com.chiho.bitvision.service.SettingService;
import com.chiho.bitvision.util.R;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("luckyjourney/file")
public class FileController implements InitializingBean {

    @Autowired
    private SettingService settingService;

    @Autowired
    private QiNiuConfig qiNiuConfig;

    @Autowired
    private FileService fileService;

    // 控制器初始化时的配置加载
    @Override
    public void afterPropertiesSet() throws Exception {
        final Setting setting = settingService
                .list(null).get(0);
        for (String s : setting.getAllowIp().split(",")) {
            LocalCache.put(s,true);
        }
    }

    // 保存文件元数据到数据库
    @PostMapping
    public R save(String fileKey){
        return R.ok().data(fileService.save(fileKey, UserHolder.get()));
    }

    // 获取文件上传所需的临时令牌
    @GetMapping("/getToken")
    public R token(String type){
        return R.ok().data(qiNiuConfig.uploadToken(type));
    }

    // 获取文件的临时授权访问链接
    @GetMapping("/{fileId}")
    public void getUUid(HttpServletRequest request, HttpServletResponse response, @PathVariable Long fileId) throws IOException, IOException {

        /*
        // 基于Referer的验证是一种常见的防盗链机制
        String ip = request.getHeader("referer");
        if (!LocalCache.containsKey(ip)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        */
        // 如果不是指定ip调用的该接口，则不返回
        File url = fileService.getFileTrustUrl(fileId);
        response.setContentType(url.getType());
        response.sendRedirect(url.getFileKey());
    }

    // 验证UUID的有效性
    @PostMapping("/auth")
    public void auth(@RequestParam(required = false) String uuid, HttpServletResponse response) throws IOException {
        if (uuid == null){
            response.sendError(403);
        }else {
            LocalCache.rem(uuid);
            response.sendError(200);
        }
    }
}
