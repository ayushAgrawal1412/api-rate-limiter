package com.learning.ratelimiter.controller;


import com.learning.ratelimiter.service.RateLimiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class RateLimiterController {

    private final RateLimiterService rateLimiterService;

    @Autowired
    public RateLimiterController(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @GetMapping("/request")
    public ResponseEntity<String> handleRequest(@RequestParam String clientID) {
        if (rateLimiterService.allowRequest(clientID)) {
            System.out.println("Request allowed successfully");
            return ResponseEntity.ok("Request allowed successfully");
        } else {
            System.out.println("Request denied due to rate limit exceeded");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Request denied due to rate limit exceeded");
        }
    }
}
