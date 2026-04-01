package com.example.nosql_database_management_system.controller;

import com.example.nosql_database_management_system.model.CollectionSchema;
import com.example.nosql_database_management_system.service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class CollectionController {
    @Autowired
    private CollectionService service;

    @PostMapping("/createCollection/{DBName}/{CollectionName}")
    public void createCol(
            @PathVariable String DBName,
            @PathVariable String CollectionName,
            @RequestBody CollectionSchema schema
            ) throws IOException {
        service.createCol(DBName, CollectionName, schema);
    }

    @DeleteMapping("/deleteCollection/{DBName}/{CollectionName}")
    public void deleteCol(
            @PathVariable String DBName,
            @PathVariable String CollectionName
    ) {
        service.deleteCol(DBName, CollectionName);
    }
}
