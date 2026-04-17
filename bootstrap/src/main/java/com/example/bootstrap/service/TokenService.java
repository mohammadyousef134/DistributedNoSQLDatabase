package com.example.bootstrap.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {

    private static final long EXPIRY_TIME = 3 * 60 * 60 * 1000; // 3 hours

    private final Map<String, Long> tokenExpiry = new ConcurrentHashMap<>();

    public String generateToken() {
        String token = UUID.randomUUID().toString();
        tokenExpiry.put(token, Instant.now().toEpochMilli() + EXPIRY_TIME);
        return token;
    }

    public boolean isValid(String token) {
        Long expiry = tokenExpiry.get(token);
        return expiry != null && expiry > Instant.now().toEpochMilli();
    }

    public void removeToken(String token) {
        tokenExpiry.remove(token);
    }

    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void cleanExpiredTokens() {
        long now = Instant.now().toEpochMilli();
        tokenExpiry.entrySet().removeIf(e -> e.getValue() < now);
    }
}