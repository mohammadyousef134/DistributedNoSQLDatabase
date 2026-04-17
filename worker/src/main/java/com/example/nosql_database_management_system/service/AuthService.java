package com.example.nosql_database_management_system.service;

import com.example.nosql_database_management_system.model.AuthenticatedUser;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
    private final Map<String, AuthenticatedUser> tokenToUser = new ConcurrentHashMap<>();

    public void addUser(String username, String token, String worker) {
        tokenToUser.put(token, new AuthenticatedUser(username, token, worker));
        System.out.println("token: " + token);

        System.out.println(tokenToUser.get(token));
    }

    public void removeUser(String token) {
        tokenToUser.remove(token);
    }

    public boolean isValid(String username, String token) {
        AuthenticatedUser user = tokenToUser.get(token);
        System.out.println("🔍 Checking auth:");
        System.out.println("Token: " + token);
        System.out.println("Stored user: " + user);
        return user != null && user.getUsername().equals(username);
    }

    public boolean isCorrectWorker(String token, String currentWorker) {
        AuthenticatedUser user = tokenToUser.get(token);
        return user != null && user.getAssignedWorker().equals(currentWorker);
    }

}
