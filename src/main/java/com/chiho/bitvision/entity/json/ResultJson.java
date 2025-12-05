package com.chiho.bitvision.entity.json;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 审核结果的顶层结构
 * 对应七牛云审核API返回的顶层结构
 */
@Data
@ToString
public class ResultJson implements Serializable {
    Integer code;
    String message;
    ResultChildJson result;
}
