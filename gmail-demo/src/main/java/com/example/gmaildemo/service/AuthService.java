package com.example.gmaildemo.service;

import com.example.gmaildemo.DAO.AuthDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {
    @Autowired
    private AuthDAO authDAO;

    public Map register(String username) {
        return authDAO.register(username);
    }
}

