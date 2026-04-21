package com.example.nosql_database_management_system.controller;

import com.example.nosql_database_management_system.exception.ForbiddenException;
import com.example.nosql_database_management_system.model.APIResponse;
import com.example.nosql_database_management_system.service.AuthService;
import com.example.nosql_database_management_system.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DatabaseController {
    @Autowired
    private DatabaseService service;
    @Autowired
    private AuthService authService;

    @PostMapping("/createDB/{db}")
    public APIResponse createDB(
            @RequestHeader String username,
            @RequestHeader String token,
            @PathVariable String db,
            @RequestParam(defaultValue = "false") boolean replicated
            ) {
        if (!authService.isAdmin(username, token)) {
            throw new ForbiddenException("Unauthorized");        }
        service.createDB(db, replicated);
        return new APIResponse(200, "Database created successfully");
    }

    @DeleteMapping("/deleteDB/{db}")
    public APIResponse deleteDB(
            @RequestHeader String username,
            @RequestHeader String token,
            @PathVariable String db,
            @RequestParam(defaultValue = "false") boolean replicated
    ) {
        if (!authService.isAdmin(username, token)) {
            throw new ForbiddenException("Unauthorized");        }
        service.deleteDB(db, replicated);
        return new APIResponse(200, "Database deleted successfully");
    }

    @GetMapping("getDBs")
    public List<String> getAllDBs() {
        return service.getAllDBs();
    }
}
