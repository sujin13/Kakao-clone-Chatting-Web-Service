package com.example.kakao_clone.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, Object> redisBlackListTemplate;

    public void set(String key, Object value) {
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.opsForValue().set(key, value);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    public boolean hasKey(String key){
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void setBlackList(String key, Object value) {
        redisBlackListTemplate.setValueSerializer(new StringRedisSerializer());
        redisBlackListTemplate.opsForValue().set(key, value);
    }

    public boolean isOnBlackList(String key) {
        return Boolean.TRUE.equals(redisBlackListTemplate.hasKey(key));
    }
}