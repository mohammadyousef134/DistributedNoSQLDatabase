package com.example.nosql_database_management_system.controller;

import com.example.nosql_database_management_system.model.APIResponse;
import com.example.nosql_database_management_system.service.DocumentService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class DocumentController {

    @Autowired
    private DocumentService service;

    // /insertOne/{db}/{collection}
    @PostMapping("/insertOne/{DBName}/{colName}")
    public APIResponse insertOne(
            @PathVariable String DBName,
            @PathVariable String colName,
            @RequestBody Map<String, Object> doc
            ) throws IOException
    {
        JSONObject jsonDoc = new JSONObject(doc);
        service.insertOne(DBName, colName, jsonDoc);
        return new APIResponse(200, "New document inserted successfully");
    }

    // /getAllDocs/{db}/{collection}
    @GetMapping("/getAllDocs/{DBName}/{colName}")
    public List<Object> getALlDocs(
            @PathVariable String DBName,
            @PathVariable String colName
    ) throws IOException {

        JSONArray array = service.getALlDocs(DBName, colName);

        List<Object> list = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            list.add(array.getJSONObject(i).toMap());
        }

        return list;
    }

//     /getDoc/{db}/{collection}/{id}
    @GetMapping("/getDoc/{DBName}/{colName}/{docId}")
    public Object getDoc(
            @PathVariable String DBName,
            @PathVariable String colName,
            @PathVariable UUID docId) throws IOException {

        return service.getDoc(DBName, colName, docId);
    }

    // /deleteDoc/{db}/{collection}/{id}
    @DeleteMapping("/deleteDoc/{DBName}/{colName}/{docId}")
    public APIResponse deleteDoc(
            @PathVariable String DBName,
            @PathVariable String colName,
            @PathVariable UUID docId) throws IOException {

        service.deleteDoc(DBName, colName, docId);
        return new APIResponse(200, "Document deleted successfully");

    }

    // /updateDoc/{db}/{collection}/{id}/{field}/{value}
    @PutMapping("/updateDoc/{DBName}/{colName}/{docId}/{field}/{newValue}")
    public APIResponse updateDoc(
            @PathVariable String DBName,
            @PathVariable String colName,
            @PathVariable UUID docId,
            @PathVariable String field,
            @PathVariable String newValue
    ) throws IOException {
        service.updateDoc(DBName, colName, docId, field, newValue);
        return new APIResponse(200, "Document updated successfully");
    }

}
