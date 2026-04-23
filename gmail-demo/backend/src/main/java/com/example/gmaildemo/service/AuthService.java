package com.example.gmaildemo.service;

import com.example.gmaildemo.DAO.AuthDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {
    @Autowired
    private AuthDAO authDAO;

    public String getWorkerForToken(String token) {
        String workerName = token.split("_", 3)[0];

        return switch (workerName) {
            case "worker1" -> "http://worker1:8081";
            case "worker2" -> "http://worker2:8082";
            default -> throw new RuntimeException("Unknown worker: " + workerName);
        };
    }
    public Map register(String username) {
        return authDAO.register(username);
    }
}

