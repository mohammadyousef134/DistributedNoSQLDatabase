package com.example.nosql_database_management_system.controller;

import com.example.nosql_database_management_system.DTO.CreateDBRequest;
import com.example.nosql_database_management_system.model.APIResponse;
import com.example.nosql_database_management_system.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DatabaseController {
    @Autowired
    private DatabaseService service;

    @PostMapping("/databases")
    public APIResponse createDB(@RequestBody CreateDBRequest request) {
        service.createDB(request.getName());
        return new APIResponse(200, "Database created successfully");
    }

    @DeleteMapping("/databases/{name}")
    public APIResponse deleteDB(@PathVariable String name) {
        service.deleteDB(name);
        return new APIResponse(200, "Database deleted successfully");
    }

    @GetMapping("/databases")
    public List<String> getAllDBs() {
        return service.getAllDBs();
    }
}
