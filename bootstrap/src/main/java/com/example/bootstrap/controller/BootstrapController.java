package com.example.bootstrap.controller;

import com.example.bootstrap.service.BootstrapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class BootstrapController {

    @Autowired
    private BootstrapService bootstrapService;

    @PostMapping("/register/{username}")
    public Map<String, String> register(@PathVariable String username) {
        return bootstrapService.registerUser(username);
    }
}