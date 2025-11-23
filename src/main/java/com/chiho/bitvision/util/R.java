package com.chiho.bitvision.util;

import lombok.Data;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * 通用响应、返回类
 * @param <T>
 */
@Data
public class R<T> implements Serializable {
    private static final long serialVersionUID = 22L;

    private T type;

    private int code;

    private Boolean state;

    private String message;

    private Object data;

    // 计数字段，可用于分页查询等场景
    private long count;

    public R(){}

    // 执行成功
    public static R ok(){
        R responseUtils = new R();
        responseUtils.setCode(0);
        responseUtils.setState(true);
        responseUtils.setMessage("成功");
        return responseUtils;
    }

    // 执行失败
    public static R error(){
        R responseUtils = new R();
        responseUtils.setCode(201);
        responseUtils.setState(false);
        responseUtils.setMessage("失败");
        return responseUtils;
    }

    public R count(long count){
        this.setCount(count);
        return this;
    }
    public R code(int code){
        this.setCode(code);
        return this;
    }

    public R state(Boolean state){
        this.setState(state);
        return this;
    }

    public R message(String message){
        this.setMessage(message);
        return this;
    }
    public R message(String message, Object... objects){
        this.setMessage(MessageFormat.format(message, objects));
        return this;
    }

    public R data(Object result){
        this.setData(result);
        return this;
    }
}
