package com.learning.ratelimiter.controller;

import com.learning.ratelimiter.RedisTokenBucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config")
public class RateLimiterConfigController {

    private final RedisTokenBucket redisTokenBucket;

    @Autowired
    public RateLimiterConfigController(RedisTokenBucket redisTokenBucket) {
        this.redisTokenBucket = redisTokenBucket;
    }

    @PostMapping("/set")
    public ResponseEntity<String> setRateLimitConfig(
            @RequestParam String clientID,
            @RequestParam int maxTokens,
            @RequestParam int refillRate,
            @RequestParam long window) {

        redisTokenBucket.setRateLimitConfig(clientID, maxTokens, refillRate, window);
        return ResponseEntity.ok("Rate limit config updated for client: " + clientID);
    }
}
