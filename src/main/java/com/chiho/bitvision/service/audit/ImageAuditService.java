package com.chiho.bitvision.service.audit;

import com.chiho.bitvision.config.QiNiuConfig;
import com.chiho.bitvision.constant.AuditStatus;
import com.chiho.bitvision.entity.Setting;
import com.chiho.bitvision.entity.json.*;
import com.chiho.bitvision.entity.response.AuditResponse;
import com.qiniu.http.Client;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.util.StringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 图片审核
 */
@Service
public class ImageAuditService extends AbstractAuditService<String, AuditResponse>{

    private static final Logger log = LoggerFactory.getLogger(ImageAuditService.class);
    static String imageUrl = "http://ai.qiniuapi.com/v3/image/censor";
    static String imageBody = "{\n" +
            "    \"data\": {\n" +
            "        \"uri\": \"${url}\"\n" +
            "    },\n" +
            "    \"params\": {\n" +
            "        \"scenes\": [\n" +
            "            \"pulp\",\n" +
            "            \"terror\",\n" +
            "            \"politician\"\n" +
            "        ]\n" +
            "    }\n" +
            "}";;

    @Override
    public AuditResponse audit(String url) {
        AuditResponse auditResponse = new AuditResponse();
        auditResponse.setAuditStatus(AuditStatus.SUCCESS);
        if (!isNeedAudit())
            return auditResponse;
        try{
            // 检查当前URL是否已经包含七牛云的自定义域名
            if (!url.contains(QiNiuConfig.CNAME)){
                String encodeFileName = URLEncoder
                        .encode(url,"utf-8")
                        .replace("+","%20");    // 编码结果从"+"替换为"%20"
                // 使用七牛云自定义域名和编码后的URL重新构建完整的URL
                url = String.format("%s/%s",QiNiuConfig.CNAME,encodeFileName);
            }
            url = appendUUID(url);  // 添加UUID认证
            // 构建七牛云API的请求体
            String body = imageBody.replace("${url}",url);
            String method = "POST";
            // 获取API请求的token
            final String token = qiNiuConfig.getToken(imageUrl,method,body,contentType);
            StringMap header = new StringMap();
            header.put("Host", "ai.qiniuapi.com");
            header.put("Authorization", token);
            header.put("Content-Type", contentType);
            Configuration cfg = new Configuration(Region.region2());    // region2为华南地区
            final Client client = new Client(cfg);
            Response response = client.post(imageUrl,body.getBytes(),header,contentType);

            final Map map = objectMapper.readValue(response.getInfo().split(" \n")[2], Map.class);
            final ResultChildJson result = objectMapper.convertValue(map.get("result"), ResultChildJson.class);
            final BodyJson bodyJson = new BodyJson();
            final ResultJson resultJson = new ResultJson();
            resultJson.setResult(result);
            bodyJson.setResult(resultJson);

            final Setting setting = settingService.getById(1);
            final SettingScoreJson settingScoreRule = objectMapper.readValue(setting.getAuditPolicy(), SettingScoreJson.class);

            final List<ScoreJson> auditRule = Arrays.asList(settingScoreRule.getManualScore(), settingScoreRule.getPassScore(), settingScoreRule.getSuccessScore());
            // 审核
            auditResponse = audit(auditRule, bodyJson);
            return auditResponse;
        }catch (Exception e){
            auditResponse.setAuditStatus(AuditStatus.SUCCESS);
            log.info("图片审核异常：{}",e.getMessage());
        }
        return auditResponse;
    }
}
