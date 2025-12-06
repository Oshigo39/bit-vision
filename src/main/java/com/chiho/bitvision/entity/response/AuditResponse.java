package com.chiho.bitvision.entity.response;

import lombok.Data;
import lombok.ToString;

/**
 * 封装审核结果的响应实体类
 * 主要在审核流程中传递和处理审核信息
 */
@Data
@ToString
public class AuditResponse {
    // 审核状态
    private Integer auditStatus;

    // 违规标记 true:违规 false:正常
    private Boolean flag;

    // 审核结果描述
    private String msg;

    // 偏移量（定位视频审核的违规位置）
    private Long offset;

    public AuditResponse(Integer auditStatus,String msg){
        this.auditStatus = auditStatus;
        this.msg = msg;
    }

    public AuditResponse(){}
}

