package com.example.nosql_database_management_system.DAO;

import com.example.nosql_database_management_system.exception.ResourceNotFoundException;
import com.example.nosql_database_management_system.exception.ValidationException;
import com.example.nosql_database_management_system.model.CollectionSchema;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@Repository
public class DocumentDAO {
    @Autowired
    private CollectionDAO collectionDAO;
    private final String BASE_PATH = "databases/";
    // insert doc
    public void insertDoc(String DBName, String colName, JSONObject doc) throws IOException {
        File file = new File(BASE_PATH + DBName + "/" + colName + ".json");
        if (!file.exists()) {
            throw new ResourceNotFoundException("Database or Collection does not exist");
        }

        JSONArray jsonArray;
        String content = new String(Files.readAllBytes(file.toPath()));
        if (content.isEmpty()) {
            jsonArray = new JSONArray();
        } else {
            jsonArray = new JSONArray(content);
        }
        jsonArray.put(doc);
        try (FileWriter fileWriter = new FileWriter(file, false)) {
            fileWriter.write(jsonArray.toString(4));
        }
    }

    // get all doc
    public JSONArray getAllDocs(String DBName, String colName) throws IOException{
        File file = new File(BASE_PATH + DBName + "/" + colName + ".json");
        System.out.println(file.getAbsolutePath());
        if (!file.exists()) {
            throw new ResourceNotFoundException("Database or Collection does not exist");
        }
        String content = Files.readString(file.toPath());
        JSONArray array = new JSONArray(content);
        return array;
    }

    // get doc
    public JSONObject getDoc(String DBName, String colName, UUID docId) throws IOException {
        File file = new File(BASE_PATH + DBName + "/" + colName + ".json");
        System.out.println(file.getAbsolutePath());
        if (!file.exists()) {
            throw new ResourceNotFoundException("Database or Collection does not exist");
        }
        String content = Files.readString(file.toPath());
        JSONArray array = new JSONArray(content);
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            if (obj.getString("id").equals(docId.toString())) {
                return obj;
            }
        }
        throw new ResourceNotFoundException("Document dose not exist");
    }

    // delete doc
    public void deleteDoc(String DBName, String colName, UUID docId) throws IOException {
        File file = new File(BASE_PATH + DBName + "/" + colName + ".json");
        System.out.println(file.getAbsolutePath());
        if (!file.exists()) {
            throw new ResourceNotFoundException("Database or Collection does not exist");
        }
        String content = Files.readString(file.toPath());
        JSONArray array = new JSONArray(content);
        JSONArray newArray = new JSONArray();
        boolean found = false;
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            if (obj.getString("id").equals(docId.toString())) {
                found = true;
                continue;
            }
            newArray.put(obj);
        }
        if (!found) {
            throw new ResourceNotFoundException("Document dose not exist");
        }
        try (FileWriter fileWriter = new FileWriter(file, false)) {
            fileWriter.write(newArray.toString(4));
        }
    }

    // update doc
    public void updateDoc(String DBName, String colName, UUID docId, String field, String newValue) throws IOException{
        File file = new File(BASE_PATH + DBName + "/" + colName + ".json");
        System.out.println(file.getAbsolutePath());
        if (!file.exists()) {
            throw new ResourceNotFoundException("Database or Collection does not exist");
        }
        String content = Files.readString(file.toPath());
        JSONArray array = new JSONArray(content);
        JSONArray newArray = new JSONArray();
        JSONObject newDoc = new JSONObject();
        boolean found = false;
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            if (obj.getString("id").equals(docId.toString())) {
                found = true;
                CollectionSchema schema = collectionDAO.getSchemaAsCollectionSchemaObject(DBName, colName);
                if (!schema.getField(field).isNullable() && newValue == null) {
                    throw new ValidationException("Null not allowed");
                }
                obj.put(field, newValue);
            }
            newArray.put(obj);
        }
        if (!found) {
            throw new ResourceNotFoundException("Document dose not exist");
        }
        try (FileWriter fileWriter = new FileWriter(file, false)) {
            fileWriter.write(newArray.toString(4));
        }
    }

}
