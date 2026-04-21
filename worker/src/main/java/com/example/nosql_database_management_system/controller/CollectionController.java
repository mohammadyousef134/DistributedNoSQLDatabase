package com.example.nosql_database_management_system.controller;

import com.example.nosql_database_management_system.exception.ForbiddenException;
import com.example.nosql_database_management_system.model.APIResponse;
import com.example.nosql_database_management_system.model.CollectionSchema;
import com.example.nosql_database_management_system.service.AuthService;
import com.example.nosql_database_management_system.service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class CollectionController {
    @Autowired
    private CollectionService service;
    @Autowired
    private AuthService authService;

    @PostMapping("/createCol/{db}/{col}")
    public APIResponse createCol(
            @RequestHeader String username,
            @RequestHeader String token,
            @PathVariable String db,
            @PathVariable String col,
            @RequestBody CollectionSchema schema,
            @RequestParam(defaultValue = "false") boolean replicated
    ) throws IOException {
        if (!authService.isAdmin(username, token)) {
            throw new ForbiddenException("Unauthorized");        }
        service.createCol(db, col, schema, replicated);
        return new APIResponse(200, "Collection created successfully");
    }

    @DeleteMapping("/deleteCol/{db}/{col}")
    public APIResponse deleteCol(
            @RequestHeader String username,
            @RequestHeader String token,
            @PathVariable String db,
            @PathVariable String col,
            @RequestParam(defaultValue = "false") boolean replicated

    ) {
        if (!authService.isAdmin(username, token)) {
            throw new ForbiddenException("Unauthorized");        }
        service.deleteCol(db, col, replicated);
        return new APIResponse(200, "Collection deleted successfully");
    }
}
