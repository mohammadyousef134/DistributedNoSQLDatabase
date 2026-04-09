package com.example.nosql_database_management_system.controller;

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


    @PostMapping("/createDB/{db}")
    public APIResponse createDB(
            @PathVariable String db,
            @RequestParam(defaultValue = "false") boolean replicated
            ) {
        service.createDB(db, replicated);
        return new APIResponse(200, "Database created successfully");
    }

    @DeleteMapping("/deleteDB/{db}")
    public APIResponse deleteDB(
            @PathVariable String db,
            @RequestParam(defaultValue = "false") boolean replicated
    ) {
        service.deleteDB(db, replicated);
        return new APIResponse(200, "Database deleted successfully");
    }

    @GetMapping("getDBs")
    public List<String> getAllDBs() {
        return service.getAllDBs();
    }
}
