package me.silvernine.jwttutorial.global.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisDao {
  private final RedisTemplate<String, String> redisTemplate;

  public void setValue(String key, String value) {
    ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
    valueOperations.set(key, value);
  }
}