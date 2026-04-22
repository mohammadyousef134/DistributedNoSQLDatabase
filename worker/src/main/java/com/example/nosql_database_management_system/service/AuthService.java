package com.example.nosql_database_management_system.service;

import com.example.nosql_database_management_system.model.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
    private final Map<String, AuthenticatedUser> tokenToUser = new ConcurrentHashMap<>();

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.token}")
    private String adminToken;

    public boolean isAdmin(String username, String token) {
        return username.equals(adminUsername) && token.equals(adminToken);
    }

    public void addUser(String username, String token, String worker) {
        tokenToUser.put(token, new AuthenticatedUser(username, token, worker));
    }

    public void removeUser(String token) {
        tokenToUser.remove(token);
    }

    public boolean isValid(String username, String token) {
        AuthenticatedUser user = tokenToUser.get(token);

        if (user == null || username == null || token == null) return false;

        String[] parts = token.split("_");
        if (parts.length != 3) return false;

        long expiry;
        try {
            expiry = Long.parseLong(parts[2]);
        } catch (Exception e) {
            return false;
        }

        if (expiry < System.currentTimeMillis()) {
            tokenToUser.remove(token);
            return false;
        }

        return user.getUsername().equals(username);
    }

    public boolean isCorrectWorker(String token, String currentWorker) {
        AuthenticatedUser user = tokenToUser.get(token);
        return user != null && user.getAssignedWorker().equals(currentWorker);
    }

}
