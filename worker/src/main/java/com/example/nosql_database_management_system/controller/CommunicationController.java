package com.example.nosql_database_management_system.controller;

import com.example.nosql_database_management_system.model.APIResponse;
import com.example.nosql_database_management_system.model.CollectionSchema;
import com.example.nosql_database_management_system.service.AuthService;
import com.example.nosql_database_management_system.service.CollectionService;
import com.example.nosql_database_management_system.service.DatabaseService;
import com.example.nosql_database_management_system.service.DocumentService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/internal")
public class CommunicationController {
    @Autowired
    private AuthService authService;
    @Autowired
    private DatabaseService dbService;
    @Autowired
    private CollectionService colService;
    @Autowired
    private DocumentService docService;

    @PostMapping("/addAuthenticatedUser/{username}/{token}/{worker}")
    public APIResponse addUser(
            @PathVariable String username,
            @PathVariable String token,
            @PathVariable String worker) {
        System.out.println("Received user from bootstrap: " + username + " token=" + token + " worker=" + worker);
        authService.addUser(username, token, worker);
        return new APIResponse(200, "User added");
    }

    @DeleteMapping("/removeAuthenticatedUser/{token}")
    public APIResponse removeUser(@PathVariable String token) {
        authService.removeUser(token);
        return new APIResponse(200, "User removed");
    }

    @PostMapping("/createDB/{name}")
    public APIResponse createDBInternal(@PathVariable String name) {
        dbService.createDB(name, true);
        return new APIResponse(200, "Database created (replicated)");
    }

    @DeleteMapping("/deleteDB/{db}")
    public APIResponse deleteDBInternal(@PathVariable String db) {
        dbService.deleteDB(db, true);
        return new APIResponse(200, "Database deleted (replicated)");
    }

    @PostMapping("/createCol/{db}/{col}")
    public APIResponse createColInternal(
            @PathVariable String db,
            @PathVariable String col,
            @RequestBody CollectionSchema schema
    ) throws IOException {
        colService.createCol(db, col, schema, true);
        return new APIResponse(200, "Collection created (replicated)");
    }

    @DeleteMapping("/deleteCol/{db}/{col}")
    public APIResponse deleteColInternal(
            @PathVariable String db,
            @PathVariable String col
    ) {
        colService.deleteCol(db, col, true);
        return new APIResponse(200, "Collection deleted (replicated)");
    }

    @PostMapping("/insert/{db}/{col}")
    public APIResponse insertInternal(
            @PathVariable String db,
            @PathVariable String col,
            @RequestBody String doc) throws Exception {

        docService.insertOne(db, col, new JSONObject(doc), true, true);
        return new APIResponse(200, "Inserted (replicated)");
    }

    @PostMapping("/delete/{db}/{col}/{id}")
    public APIResponse deleteInternal(
            @PathVariable String db,
            @PathVariable String col,
            @PathVariable String id) throws Exception {

        docService.deleteDoc(db, col, UUID.fromString(id), true, true);
        return new APIResponse(200, "Deleted (replicated)");
    }

    @PostMapping("/update/{db}/{col}/{id}")
    public APIResponse updateInternal(
            @PathVariable String db,
            @PathVariable String col,
            @PathVariable String id,
            @RequestParam String field,
            @RequestParam String value,
            @RequestParam int version) throws Exception {

        docService.updateDoc(
                db, col, UUID.fromString(id),
                field, value, version,
                true, true
        );

        return new APIResponse(200, "Updated (replicated)");
    }

}
