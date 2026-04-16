package com.example.nosql_database_management_system.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticatedUser {
    private String username;
    private String token;
    private String assignedWorker;
}