package com.example.nosql_database_management_system.controller;

import com.example.nosql_database_management_system.model.APIResponse;
import com.example.nosql_database_management_system.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal")
public class CommunicationController {
    @Autowired
    private AuthService authService;

    @PostMapping("/addAuthenticatedUser/{username}/{token}/{worker}")
    public APIResponse addUser(
            @PathVariable String username,
            @PathVariable String token,
            @PathVariable String worker) {
        authService.addUser(username, token, worker);
        return new APIResponse(200, "User added");
    }

    @DeleteMapping("/removeAuthenticatedUser/{token}")
    public APIResponse removeUser(@PathVariable String token) {
        authService.removeUser(token);
        return new APIResponse(200, "User removed");
    }
}
