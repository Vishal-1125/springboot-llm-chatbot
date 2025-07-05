package com.vishalaneja.chatbot.service.impl;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.vishalaneja.chatbot.service.CacheService;

@Service
public class RedisCacheServiceImpl implements CacheService {

	private static final Duration TTL = Duration.ofMinutes(10);
	private static final String KEY_PREFIX = "chatbot::";

	private final RedisTemplate<String, String> redisTemplate;

	@Autowired
	public RedisCacheServiceImpl(RedisTemplate<String, String> redisTemplate) {
		super();
		this.redisTemplate = redisTemplate;
	}

	@Override
	public String getFromCache(String prompt) {
		return redisTemplate.opsForValue().get(KEY_PREFIX+prompt.trim().toLowerCase());
	}

	@Override
	public void saveToCache(String prompt, String response) {
		redisTemplate.opsForValue().set(KEY_PREFIX+prompt.trim().toLowerCase(), response , TTL);

	}

}
