package com.chiho.bitvision.entity.json;

import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 处理七牛云内容审核返回的Json内容的类
 * 提供统一的审核结果解析和违规检查逻辑
 */
@Data
public class BodyJson implements Serializable {
    // 审核任务id
    String id;
    // 审核任务状态
    String status;
    // 审核结果详情
    ResultJson result;

    // 检查审核分数是否在违规范围内
    public boolean compare(Double min, Double max, Double value){
        return value >= min && value <= max;
    }

    // 检查图片审核结果是否违规
    public boolean checkViolation(List<CutsJson> types, Double min, Double max){
        for (CutsJson cutsJson : types){
            if (!ObjectUtils.isEmpty(cutsJson.details)){
                for (DetailsJson detail : cutsJson.details){
                    if (compare(min,max,detail.getScore())){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // 获取恐怖内容的审核结果用于违规检查
    public List<CutsJson> getTerror(){
        final TypeJson terror = result.getResult().getScenes().getTerror();
        if (!ObjectUtils.isEmpty(terror.getCuts())){
            return terror.getCuts();
        }

        final CutsJson cutsJson = new CutsJson();
        cutsJson.setDetails(terror.getDetails());
        cutsJson.setSuggestion(terror.getSuggestion());
        return Collections.singletonList(cutsJson);
    }

    // 政治
    public List<CutsJson> getPolitician(){
        final TypeJson politician = result.getResult().getScenes().getPolitician();
        if (!ObjectUtils.isEmpty(politician.getCuts())){
            return politician.getCuts();
        }

        final CutsJson cutsJson = new CutsJson();
        cutsJson.setDetails(politician.getDetails());
        cutsJson.setSuggestion(politician.getSuggestion());

        return Collections.singletonList(cutsJson);
    }

    // 色情
    public List<CutsJson> getPulp(){
        final TypeJson pulp = result.getResult().getScenes().getPulp();
        if (!ObjectUtils.isEmpty(pulp.getCuts())){
            return pulp.cuts;
        }

        final CutsJson cutsJson = new CutsJson();
        cutsJson.setDetails(pulp.getDetails());
        cutsJson.setSuggestion(pulp.getSuggestion());

        return Collections.singletonList(cutsJson);
    }
}
