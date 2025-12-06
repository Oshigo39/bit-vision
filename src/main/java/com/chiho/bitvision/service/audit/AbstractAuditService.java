package com.chiho.bitvision.service.audit;

import com.chiho.bitvision.config.LocalCache;
import com.chiho.bitvision.config.QiNiuConfig;
import com.chiho.bitvision.constant.AuditMsgMap;
import com.chiho.bitvision.constant.AuditStatus;
import com.chiho.bitvision.entity.Setting;
import com.chiho.bitvision.entity.json.*;
import com.chiho.bitvision.entity.response.AuditResponse;
import com.chiho.bitvision.service.SettingService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.UUID;

/**
 * 通用的审核逻辑
 * 基于ScoreJson列表进行审核，支持灵活的审核规则配置
 * - 先根据分数阈值进行精确审核
 * - 如果没有匹配到规则，再根据七牛云的suggestion字段进行兜底检查
 * 资源访问
 * - 通过appendUUID方法实现简单的资源访问认证机制
 * - 结合LocalCache实现UUID的临时存储和验证
 * @param <T>
 * @param <R>
 */
@Service
public abstract class AbstractAuditService<T,R> implements AuditService<T,R> {

    @Autowired
    protected QiNiuConfig qiNiuConfig;

    @Autowired
    protected SettingService settingService;

    protected ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

    static final String contentType = "application/json";

    /**
     * 综合审核
     * @param scoreJsonList 审核规则列表
     * @param bodyJson 七牛云审核API返回的原始审核结果
     * @return 包含最终审核状态、违规信息等
     */
    protected AuditResponse audit(List<ScoreJson> scoreJsonList, BodyJson bodyJson) {
        AuditResponse audit = new AuditResponse();
        // 遍历的是通过,人工,失败的审核规则,我当前没有办法知道是什么状态
        for (ScoreJson scoreJson : scoreJsonList) {
            audit = audit(scoreJson, bodyJson);
            // 如果为true,说明命中得分，提前返回
            if (audit.getFlag()){
                audit.setAuditStatus(scoreJson.getAuditStatus());
                return audit;
            }
        }
        // 如果出来了说明审核的内容没分数 / 审核比例没调好(人员问题)
        // 比较suggest
        final ScenesJson scenes = bodyJson.getResult().getResult().getScenes();
        // 如果没有命中得分，调用endCheck方法根据七牛云的suggestion字段进行最终检查
        if (endCheck(scenes)){
            audit.setAuditStatus(AuditStatus.SUCCESS);
        }else {
            audit.setAuditStatus(AuditStatus.PASS);
            audit.setMsg("内容不合法");
        }
        return audit;
    }

    /**
     * 从审核结果中提取违规信息
     * @param types 审核类型列表
     * @param minPolitician 最小分数阈值
     * @return 包含违规信息、偏移量等
     */
    private AuditResponse getInfo(List<CutsJson> types, Double minPolitician, String key) {
        AuditResponse auditResponse = new AuditResponse();
        auditResponse.setFlag(false);
        String info = null;
        // 遍历审核类型列表，检查每个审核项的分数
        for (CutsJson type : types) {
            for (DetailsJson detail : type.getDetails()) {
                // 人工/PASS ? 交给七牛云状态，我只获取信息和offset
                if (detail.getScore() > minPolitician) {
                    // 如果违规,则填充额外信息
                    if (!detail.getLabel().equals(key)) {
                        info = AuditMsgMap.getInfo(detail.getLabel());
                        auditResponse.setMsg(info);
                        auditResponse.setOffset(type.getOffset());
                    }
                    auditResponse.setFlag(true);
                }

            }
        }
        if (auditResponse.getFlag() && ObjectUtils.isEmpty(auditResponse.getMsg())){
            auditResponse.setMsg("该视频违反比特视界平台规则!");
        }

        return auditResponse;
    }


    /**
     * 根据单个审核规则进行审核
     * @param scoreJson 单个审核规则
     * @param bodyJson 七牛云审核API返回的原始审核结果
     * @return 审核结果
     */
    private AuditResponse audit(ScoreJson scoreJson, BodyJson bodyJson) {

        /*
        * 分别获取政治人物、色情、恐怖内容的审核结果
        * 检查每种内容是否违反当前审核规则
        * 如果违反规则，则调用 getInfo 方法获取违规信息
        * 如果所有内容都符合规则，则返回正常结果
        * */
        AuditResponse auditResponse = new AuditResponse();
        auditResponse.setFlag(true);
        auditResponse.setAuditStatus(scoreJson.getAuditStatus());

        final Double minPolitician = scoreJson.getMinPolitician();
        final Double maxPolitician = scoreJson.getMaxPolitician();
        final Double minPulp = scoreJson.getMinPulp();
        final Double maxPulp = scoreJson.getMaxPulp();
        final Double minTerror = scoreJson.getMinTerror();
        final Double maxTerror = scoreJson.getMaxTerror();

        // 所有都要比较,如果返回的有问题则直接返回
        if (!ObjectUtils.isEmpty(bodyJson.getPolitician())) {
            if (bodyJson.checkViolation(bodyJson.getPolitician(),minPolitician,maxPolitician)) {
                final AuditResponse response = getInfo(bodyJson.getPolitician(), minPolitician, "group");
                auditResponse.setMsg(response.getMsg());
                if (response.getFlag()) {
                    auditResponse.setOffset(response.getOffset());
                    return auditResponse;
                }
            }
        }
        if (!ObjectUtils.isEmpty(bodyJson.getPulp())) {
            if (bodyJson.checkViolation(bodyJson.getPulp(),minPulp,maxPulp)) {
                final AuditResponse response = getInfo(bodyJson.getPulp(), minPulp, "normal");
                auditResponse.setMsg(response.getMsg());
                // 如果违规则提前返回
                if (response.getFlag()) {
                    auditResponse.setOffset(response.getOffset());
                    return auditResponse;
                }
            }
        }
        if (!ObjectUtils.isEmpty(bodyJson.getTerror())) {
            if (bodyJson.checkViolation(bodyJson.getTerror(),minTerror,maxTerror)) {
                final AuditResponse response = getInfo(bodyJson.getTerror(), minTerror, "normal");
                auditResponse.setMsg(response.getMsg());
                if (response.getFlag()) {
                    auditResponse.setOffset(response.getOffset());
                    return auditResponse;
                }
            }
        }
        auditResponse.setMsg("正常");
        auditResponse.setFlag(false);
        return auditResponse;
    }

    /**
     * 在没有匹配到审核规则时，根据七牛云的suggestion字段进行最终检查
     * @param scenes 审核场景信息
     * @return 表示审核是否通过
     */
    private boolean endCheck(ScenesJson scenes){
        final TypeJson terror = scenes.getTerror();
        final TypeJson politician = scenes.getPolitician();
        final TypeJson pulp = scenes.getPulp();

        return !terror.getSuggestion().equals("block")
                && !politician.getSuggestion().equals("block")
                && !pulp.getSuggestion().equals("block");
    }

    /**
     * 根据系统配置表查询是否需要审核
     * @return ?
     */
    protected Boolean isNeedAudit(){
        final Setting setting = settingService.list(null).get(0);
        return setting.getAuditOpen();
    }


    /**
     * 给七牛云资源URL添加UUID参数，用于资源访问认证
     * @param url 原始七牛云资源URL
     * @return 添加了UUID的URL
     */
    protected String appendUUID(String url){

        final Setting setting = settingService.list(null).get(0);

        if (setting.getAuth()) {
            final String uuid = UUID.randomUUID().toString();
            LocalCache.put(uuid,true);
            if (url.contains("?")){
                url = url+"&uuid="+uuid;
            }else {
                url = url+"?uuid="+uuid;
            }
            return url;
        }
        return url;
    }
}
