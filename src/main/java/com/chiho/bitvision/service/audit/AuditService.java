package com.chiho.bitvision.service.audit;

public interface AuditService<T,R> {
    /**
     * 抽象审核接口规范
     * @param task 繁星
     * @return response
     */
    R audit(T task);
}
