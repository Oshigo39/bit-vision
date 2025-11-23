package com.chiho.bitvision.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Service(value = "redisCacheUtil")
public class RedisCacheUtil {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheUtil.class);
    // 在 redisConfig 当中声明为 bean 的 redisTemplate
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 普通缓存放入方法
     * @param key redis 的 key
     * @param value redis 的 value
     * @return 成功与否
     */
    public boolean set(String key, Object value){
        try{
            redisTemplate.opsForValue().set(key, value);
            return true;
        }catch (Exception e){
            log.error("redis普通缓存设置失败：{}",e.getMessage());
            return false;
        }
    }

    /**
     * 普通缓存放入，并设置过期时间
     * @param key redis 的 key
     * @param value redis 的 value
     * @return 成功与否
     */
    public boolean set(String key, Object value,long time){
        try{
            if (time > 0){
                redisTemplate.opsForValue().set(key, value,time, TimeUnit.SECONDS);
            }else {
                set(key,value);
            }
            return true;
        }catch (Exception e){
            log.error("redis带时间普通缓存设置失败：{}",e.getMessage());
        }
        return false;
    }

    /**
     * 普通缓存获取
     * @param key 传入的key键值
     * @return 值
     */
    public Object get(String key){
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除缓存
     * @param key 传入的key
     */
    public void del(String... key){
        if (key != null && key.length > 0){
            if (key.length == 1){
                redisTemplate.delete(key[0]);
            }else {
                redisTemplate.delete(Arrays.asList(key));
            }
        }
    }
}
