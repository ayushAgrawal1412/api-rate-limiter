package com.learning.ratelimiter;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisTokenBucket {

    private final StringRedisTemplate redisTemplate;

    private static final String PREFIX = "rate-limiter:";
    private static final String CONFIG_PREFIX = "rate-limiter-config:";

    // Default values (used if no config found)
    private final int defaultMaxTokens = 2;
    private final int defaultRefillRate = 2;
    private final long defaultWindow = 60; // 60 seconds

    public RedisTokenBucket(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean allowRequest(String clientID) {
        String key = PREFIX + clientID;

        // Read current token count
        String value = redisTemplate.opsForValue().get(key);
        int tokens = (value == null) ? getMaxTokens(clientID) : Integer.parseInt(value);

        // If tokens are available, consume one
        if (tokens > 0) {
            redisTemplate.opsForValue().set(key, String.valueOf(tokens - 1), getWindow(clientID), TimeUnit.SECONDS);
            return true;
        }
        return false;
    }

    public void refillTokens() {
        for (String key : redisTemplate.keys(PREFIX + "*")) {
            String clientID = key.replace(PREFIX, "");
            String value = redisTemplate.opsForValue().get(key);
            int tokens = (value == null) ? getMaxTokens(clientID) : Integer.parseInt(value);

            int refillRate = getRefillRate(clientID);
            int maxTokens = getMaxTokens(clientID);

            int newTokens = Math.min(maxTokens, tokens + refillRate);
            redisTemplate.opsForValue().set(key, String.valueOf(newTokens), getWindow(clientID), TimeUnit.SECONDS);
        }
    }

    // Fetch maxTokens from Redis configuration
    private int getMaxTokens(String clientID) {
        Object value = redisTemplate.opsForHash().get(CONFIG_PREFIX + clientID, "maxTokens");
        return (value != null) ? Integer.parseInt(value.toString()) : defaultMaxTokens;
    }

    // Fetch refillRate from Redis configuration
    private int getRefillRate(String clientID) {
        Object value = redisTemplate.opsForHash().get(CONFIG_PREFIX + clientID, "refillRate");
        return (value != null) ? Integer.parseInt(value.toString()) : defaultRefillRate;
    }

    // Fetch window size from Redis configuration
    private long getWindow(String clientID) {
        Object value = redisTemplate.opsForHash().get(CONFIG_PREFIX + clientID, "window");
        return (value != null) ? Long.parseLong(value.toString()) : defaultWindow;
    }

    // ✅ Method to set configuration (can be called from an endpoint)
    public void setRateLimitConfig(String clientID, int maxTokens, int refillRate, long window) {
        redisTemplate.opsForHash().put(CONFIG_PREFIX + clientID, "maxTokens", String.valueOf(maxTokens));
        redisTemplate.opsForHash().put(CONFIG_PREFIX + clientID, "refillRate", String.valueOf(refillRate));
        redisTemplate.opsForHash().put(CONFIG_PREFIX + clientID, "window", String.valueOf(window));
    }
}

