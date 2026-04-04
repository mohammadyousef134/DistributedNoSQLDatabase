package com.example.nosql_database_management_system.DAO;

import com.example.nosql_database_management_system.exception.ForbiddenException;
import com.example.nosql_database_management_system.exception.ResourceNotFoundException;
import com.example.nosql_database_management_system.model.CollectionSchema;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

@Repository
public class CollectionDAO {
    private final String BASE_PATH = "databases/";

    // create collection
    public void createCol(String DBName, String ColName, JSONObject schema) throws IOException {

        File schemasPath = new File(BASE_PATH + DBName + "/schemas");

        if (!schemasPath.exists() && !schemasPath.mkdirs()) {
            throw new ForbiddenException("Failed to create schema directory");
        }

        File schemaFile = new File(schemasPath, ColName + ".json");

        if (schemaFile.exists()) {
            throw new ForbiddenException("Collection is already existed");
        }

        try (FileWriter writer = new FileWriter(schemaFile)) {
            writer.write(schema.toString(4));
        }

        File collectionFile = new File(BASE_PATH + DBName + "/" + ColName + ".json");

        if (!collectionFile.createNewFile()) {
            throw new ForbiddenException("Failed to create collection file");
        }
    }


    // delete collection
    public void deleteCol(String DBName, String ColName) {

        // schema file
        File schemaFile = new File(BASE_PATH + DBName + "/schemas/" + ColName + ".json");

        if (!schemaFile.exists()) {
            throw new ResourceNotFoundException("Collection does not exist");
        }

        if (!schemaFile.delete()) {
            throw new ForbiddenException("Failed to delete schema file");
        }

        // collection file
        File collectionFile = new File(BASE_PATH + DBName + "/" + ColName + ".json");
        if (collectionFile.exists() && !collectionFile.delete()) {
            throw new ForbiddenException("Failed to delete collection file");
        }
    }

    // get schema
    public CollectionSchema getSchemaAsCollectionSchemaObject(String DBName, String ColName) throws IOException {
        File file = new File(BASE_PATH + DBName + "/schemas/" + ColName + ".json");

        if (!file.exists()) {
            throw new ResourceNotFoundException("Collection does not exist");
        }

        String content = new String(Files.readAllBytes(file.toPath()));

        if (content.isEmpty()) {
            throw new RuntimeException("Schema file is empty");
        }

        JSONObject jsonObject = new JSONObject(content);
        return CollectionSchema.fromJSON(jsonObject);
    }

}
