package com.learning.ratelimiter;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {

    private final RedisTokenBucket redisTokenBucket;

    public RateLimiterService(RedisTokenBucket redisTokenBucket) {
        this.redisTokenBucket = redisTokenBucket;
    }

    public boolean allowRequest(String clientID) {
        return redisTokenBucket.allowRequest(clientID);
    }

    @Scheduled(initialDelay = 5000, fixedRate = 10000)
    public void refillBuckets() {
        redisTokenBucket.refillTokens();
    }
}
