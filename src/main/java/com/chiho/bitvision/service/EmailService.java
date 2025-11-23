package com.chiho.bitvision.service;

/**
 * 邮箱接口
 */
public interface EmailService {

    /**
     * 邮件发送方法
     * @param email 邮箱地址
     * @param context 邮件内容
     */
    void send(String email,String context);
}
