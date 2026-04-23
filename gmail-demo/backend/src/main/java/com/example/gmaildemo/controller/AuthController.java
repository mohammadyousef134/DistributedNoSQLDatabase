package com.example.gmaildemo.controller;

import com.example.gmaildemo.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register/{username}")
    public ResponseEntity<?> register(@PathVariable String username) {
        return ResponseEntity.ok(authService.register(username));
    }
}