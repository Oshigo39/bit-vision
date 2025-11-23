package com.chiho.bitvision.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 异常基础类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseException extends RuntimeException{
    String msg;

    public BaseException(String msg){
        this.msg = msg;
    }
}
