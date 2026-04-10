package com.example.nosql_database_management_system.controller;

import com.example.nosql_database_management_system.model.APIResponse;
import com.example.nosql_database_management_system.service.DocumentService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api")
public class DocumentController {

    @Autowired
    private DocumentService service;

    // /insertOne/{db}/{collection}
    @PostMapping("/insertOne/{db}/{col}")
    public APIResponse insertOne(
            @PathVariable String db,
            @PathVariable String col,
            @RequestBody String doc,
            @RequestParam(defaultValue = "false") boolean forwarded,
            @RequestParam(defaultValue = "false") boolean replicated
    ) throws IOException
    {
        JSONObject jsonDoc = new JSONObject(doc);
        service.insertOne(db, col, jsonDoc, forwarded, replicated);
        return new APIResponse(200, "New document inserted successfully");
    }

    // /getAllDocs/{db}/{collection}
    @GetMapping("/getAllDocs/{db}/{col}")
    public List<Object> getALlDocs(
            @PathVariable String db,
            @PathVariable String col
    ) throws IOException {

        JSONArray array = service.getALlDocs(db, col);

        List<Object> list = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            list.add(array.getJSONObject(i).toMap());
        }

        return list;
    }

//     /getDoc/{db}/{collection}/{id}
    @GetMapping("/getDoc/{db}/{col}/{docId}")
    public Object getDoc(
            @PathVariable String db,
            @PathVariable String col,
            @PathVariable UUID docId) throws IOException {

        return service.getDoc(db, col, docId);
    }

    // /deleteDoc/{db}/{collection}/{id}
    @DeleteMapping("/deleteDoc/{db}/{col}/{docId}")
    public APIResponse deleteDoc(
            @PathVariable String db,
            @PathVariable String col,
            @PathVariable UUID docId,
            @RequestParam(defaultValue = "false") boolean forwarded,
            @RequestParam(defaultValue = "false") boolean replicated
    ) throws IOException {

        service.deleteDoc(db, col, docId, forwarded, replicated);
        return new APIResponse(200, "Document deleted successfully");

    }

    // /updateDoc/{db}/{collection}/{id}/{field}/{value}
    @PutMapping("/updateDoc/{db}/{col}/{docId}/{field}/{value}/{version}")
    public APIResponse updateDoc(
            @PathVariable String db,
            @PathVariable String col,
            @PathVariable UUID docId,
            @PathVariable String field,
            @PathVariable String value,
            @PathVariable int version,
            @RequestParam(defaultValue = "false") boolean forwarded,
            @RequestParam(defaultValue = "false") boolean replicated
    ) throws IOException {
        service.updateDoc(db, col, docId, field, value, version, forwarded, replicated);
        return new APIResponse(200, "Document updated successfully");
    }

    @GetMapping("/filter/{db}/{col}/{field}/{value}")
    public List<Map<String, Object>> filter(
            @PathVariable String db,
            @PathVariable String col,
            @PathVariable String field,
            @PathVariable String value
            ) {
        List<Map<String, Object>> list = service.filter(db, col, field, value);
        return list;
    }

}
