package com.example.nosql_database_management_system.model;

import lombok.Getter;

@Getter
public class APIResponse {
    private int status;
    private String message;
    public APIResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
