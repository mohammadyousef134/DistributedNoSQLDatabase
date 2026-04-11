package com.example.nosql_database_management_system.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
    private final Map<String, String> tokenToUser = new ConcurrentHashMap<>();

    public void addUser(String username, String token) {
        tokenToUser.put(token, username);
    }

    public void removeUser(String token) {
        tokenToUser.remove(token);
    }

    public boolean isValid(String username, String token) {
        return tokenToUser.containsKey(token) && tokenToUser.get(token).equals(username);
    }
}
