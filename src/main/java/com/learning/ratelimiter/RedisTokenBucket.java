package com.learning.ratelimiter;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisTokenBucket {

    private final StringRedisTemplate redisTemplate;

    private final int maxTokens = 2;  // Default max tokens per client
    private static final String PREFIX = "rate-limiter:";

    public RedisTokenBucket(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean allowRequest(String clientID) {
        String key = PREFIX + clientID;

        // Get current tokens from Redis
        String value = redisTemplate.opsForValue().get(key);
        int tokens = (value == null) ? maxTokens : Integer.parseInt(value);

        // If tokens are available, consume one
        if (tokens > 0) {
            redisTemplate.opsForValue().set(key, String.valueOf(tokens - 1), 60, TimeUnit.SECONDS);
            return true;
        }

        return false;
    }

    public void refillTokens() {
        for (String key : redisTemplate.keys(PREFIX + "*")) {
            String value = redisTemplate.opsForValue().get(key);
            int tokens = (value == null) ? maxTokens : Integer.parseInt(value);

            // Tokens refilled per second
            int refillRate = 2;
            int newTokens = Math.min(maxTokens, tokens + refillRate);

            redisTemplate.opsForValue().set(key, String.valueOf(newTokens), 60, TimeUnit.SECONDS);
        }
    }
}
