package com.chiho.bitvision.service.audit;

import com.chiho.bitvision.constant.AuditMsgMap;
import com.chiho.bitvision.constant.AuditStatus;
import com.chiho.bitvision.entity.json.DetailsJson;
import com.chiho.bitvision.entity.json.ResultChildJson;
import com.chiho.bitvision.entity.response.AuditResponse;
import com.qiniu.http.Client;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.util.StringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

@Service
public class TextAuditService extends AbstractAuditService<String, AuditResponse>{

    private static final Logger log = LoggerFactory.getLogger(TextAuditService.class);
    static String textUrl = "http://ai.qiniuapi.com/v3/text/censor";
    static String textBody = "{\n" +
            "    \"data\": {\n" +
            "        \"text\": \"${text}\"\n" +
            "    },\n" +
            "    \"params\": {\n" +
            "        \"scenes\": [\n" +
            "            \"antispam\"\n" +
            "        ]\n" +
            "    }\n" +
            "}";

    @Override
    public AuditResponse audit(String text) {

        AuditResponse auditResponse = new AuditResponse();
        auditResponse.setAuditStatus(AuditStatus.SUCCESS);

        if (!isNeedAudit()) {
            return auditResponse;
        }

        String body = textBody.replace("${text}", text);
        String method = "POST";
        // 获取token
        final String token = qiNiuConfig.getToken(textUrl, method, body, contentType);
        StringMap header = new StringMap();
        header.put("Host", "ai.qiniuapi.com");
        header.put("Authorization", token);
        header.put("Content-Type", contentType);
        Configuration cfg = new Configuration(Region.region2());
        final Client client = new Client(cfg);
        try{
            Response response = client.post(textUrl, body.getBytes(), header, contentType);

            final Map map = objectMapper.readValue(response.getInfo().split(" \n")[2], Map.class);
            final ResultChildJson result = objectMapper.convertValue(map.get("result"), ResultChildJson.class);
            auditResponse.setAuditStatus(AuditStatus.SUCCESS);
            // 文本审核直接审核suggestion
            if (!result.getSuggestion().equals("pass")) {
                auditResponse.setAuditStatus(AuditStatus.PASS);
                final List<DetailsJson> details = result.getScenes().getAntispam().getDetails();
                if (!ObjectUtils.isEmpty(details)) {
                    // 遍历找到有问题的
                    for (DetailsJson detail : details) {
                        if (!detail.getLabel().equals("normal")) {
                            auditResponse.setMsg(AuditMsgMap.getInfo(detail.getLabel()) + "\n");
                        }
                    }
                }
            }
        }catch (Exception e){
            log.error("文本审查失败：{}",e.getMessage());
        }


        return auditResponse;
    }
}
