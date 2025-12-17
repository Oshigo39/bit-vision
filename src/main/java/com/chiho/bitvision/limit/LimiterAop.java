package com.chiho.bitvision.limit;

import com.chiho.bitvision.constant.RedisConstant;
import com.chiho.bitvision.holder.UserHolder;
import com.chiho.bitvision.util.RedisCacheUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import javax.naming.LimitExceededException;

@Aspect
public class LimiterAop {

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    /**
     * 拦截
     * @param joinPoint joinPoint
     * @param limiter limiter
     * @return object
     * @throws Throwable throw
     */
    @Before("@annotation(limiter)")
    public Object restriction(ProceedingJoinPoint joinPoint, Limit limiter) throws Throwable {
        final Long userId = UserHolder.get();
        final int limitCount = limiter.limit();
        final String msg = limiter.msg();
        final long time = limiter.time();
        // 缓存是否存在
        String key = RedisConstant.VIDEO_LIMIT + userId;
        final Object o1 = redisCacheUtil.get(key);
        if (ObjectUtils.isEmpty(o1)){
            redisCacheUtil.set(key, 1, time);
        }else {
            if (Integer.parseInt(o1.toString()) > limitCount) {
                throw new LimitExceededException(msg);
            }
            redisCacheUtil.incr(key, 1);
        }
        return joinPoint.proceed();
    }
}
