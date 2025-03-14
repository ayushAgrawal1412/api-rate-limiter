package com.learning.ratelimiter;


import org.springframework.beans.factory.annotation.Autowired;
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
    public String handleRequest(@RequestParam String clientID) {
        if (rateLimiterService.allowRequest(clientID)) {
            System.out.println("Requst allowed successfully");
            return "Requst allowed successfully";
        } else {
            System.out.println("Request denied due to rate limit exceeded");
            return "Request denied due to rate limit exceeded";
        }
    }
}
