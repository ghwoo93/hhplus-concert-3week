package io.hhplus.concert.reservation.config;

import org.springframework.context.annotation.Configuration;

import io.jsonwebtoken.io.IOException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import redis.embedded.RedisServer;

@Configuration
public class TestRedisConfiguration {
    private RedisServer redisServer;

    @PostConstruct
    public void postConstruct() throws IOException {
        redisServer = new RedisServer(6379);
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }
}
