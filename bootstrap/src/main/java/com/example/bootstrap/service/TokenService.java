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

    public String generateToken() {
        long expiry = Instant.now().toEpochMilli() + EXPIRY_TIME;
        return UUID.randomUUID() + "_" + expiry;
    }

}