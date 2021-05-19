package cn.xhalo.blog.auth.server.service;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @Author: xhalo
 * @Date: 2021/5/18 1:39 下午
 * @Description:
 */
public class AuthRedisService {

    private final RedisTemplate redisTemplate;

    public AuthRedisService(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public <T> T get(String key, Class<T> clazz) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    public <T> T getAndSet(String key, Object value, Class<T> clazz) {
        return (T) redisTemplate.opsForValue().getAndSet(key, value);
    }

    public String getString(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    /**
     * setex，秒为单位
     * @param key
     * @param value
     * @param expire
     */
    public void setEx(String key, Object value, Long expire) {
        redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
    }

    /**
     * setex，秒为单位
     * @param key
     * @param value
     * @param expire
     */
    public void setEx(String key, Object value, Long expire, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, expire, timeUnit);
    }

    public boolean existKey(String key) {
        return redisTemplate.opsForValue().get(key) != null;
    }

    public boolean delete(String key) {
        return redisTemplate.opsForValue().getOperations().delete(key);
    }
}
