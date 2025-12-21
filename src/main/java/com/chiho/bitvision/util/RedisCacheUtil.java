package com.chiho.bitvision.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
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

    /**
     * 操作Redis有序集合ZSet的工具方法；
     * 获取Redis有序集合中所有元素，并按分数降序排列；
     * 有序集合：
     * - 每个元素都关联一个double类型的分数(score)；
     * - 支持按分数进行排序和范围查询；
     * - 适用于需要排序的场景，如排行榜、时间线等；
     * @param key key
     * @return obj
     */
    public Set<Object> zGet(String key) {
        // 0，-1表示获取所有元素，reverseRange按照分数降序返回
        return redisTemplate.opsForZSet().reverseRange(key,0,-1);
    }

    /**
     * 实现Redis有序集合(ZSet)分页查询
     * @param key 分页查询对象
     * @param pageNum page number
     * @param pageSize page size
     * @return obj
     */
    public Set<ZSetOperations.TypedTuple<Object>> zSetGetByPage(String key, long pageNum, long pageSize) {
        try {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                long start = (pageNum - 1) * pageSize;  // 计算开始索引
                long end = pageNum * pageSize - 1;  // 计算结算书索引
                Long size = redisTemplate.opsForZSet().size(key);
                if (end > size) {   // 处理边界问题
                    end = -1;   // -1表示到集合末尾
                }
                // 按分数降序获取指定范围的元素及分数
                return redisTemplate.opsForZSet().reverseRangeWithScores(key,start,end);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("分页获取数据失败：{}",e.getMessage());
            return null;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return long
     */
    public Long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("缓存放入Redis失败：{}",e.getMessage());
            return 0;
        }
    }

    /**
     * 添加有序集合
     *
     * @param key key
     * @param score score
     * @param val val
     * @param time time
     */
    public void zadd(String key, double score, Object val, long time) {
        redisTemplate.opsForZSet().add(key, val, score);

        this.expire(key, time);
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return ?
     */
    public boolean expire(String key, long time) {

        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error("指定缓存失效时间失败：{}",e.getMessage());
            return false;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            log.error("移除失败：{}",e.getMessage());
            return 0;
        }
    }
}
