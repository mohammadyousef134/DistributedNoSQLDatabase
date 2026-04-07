package com.example.nosql_database_management_system.DAO;

import com.example.nosql_database_management_system.DAO.indexing.PropertyIndexManager;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class DocumentDAO {
    @Autowired
    private CollectionDAO collectionDAO;
    @Autowired
    private PropertyIndexManager indexManager;

    private final String BASE_PATH = "databases/";
    // insert doc
    public void insertDoc(String db, String col, JSONObject doc) throws IOException {
        File file = new File(BASE_PATH + db + "/" + col + ".json");
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
        indexManager.addToIndex(db, col, doc);
    }

    // get all doc
    public JSONArray getAllDocs(String db, String col) throws IOException{
        File file = new File(BASE_PATH + db + "/" + col + ".json");
        if (!file.exists()) {
            throw new ResourceNotFoundException("Database or Collection does not exist");
        }
        String content = Files.readString(file.toPath());
        JSONArray array = new JSONArray(content);
        return array;
    }

    // get doc
    public JSONObject getDoc(String db, String col, UUID docId) throws IOException {
        File file = new File(BASE_PATH + db + "/" + col + ".json");
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
    public void deleteDoc(String db, String col, UUID docId) throws IOException {
        File file = new File(BASE_PATH + db + "/" + col + ".json");
        if (!file.exists()) {
            throw new ResourceNotFoundException("Database or Collection does not exist");
        }
        String content = Files.readString(file.toPath());
        JSONArray array = new JSONArray(content);
        JSONArray newArray = new JSONArray();
        boolean found = false;
        JSONObject object = new JSONObject();
        for (int i = 0; i < array.length(); i++) {

            JSONObject obj = array.getJSONObject(i);
            if (obj.getString("id").equals(docId.toString())) {
                found = true;
                object = obj;
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
        indexManager.removeFromIndex(db, col, object);
    }

    // update doc
    public void updateDoc(String db, String col, UUID docId, String field, String newValue) throws IOException{
        File file = new File(BASE_PATH + db + "/" + col + ".json");
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
                CollectionSchema schema = collectionDAO.getSchemaAsCollectionSchemaObject(db, col);
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
        indexManager.addToIndex(db, col, newDoc);
    }

    public List<Map<String, Object>> filter(String db, String col, String field, String value) {

        List<JSONObject> docs = indexManager.search(db, col, field, value);

        List<Map<String, Object>> result = new ArrayList<>();

        for (JSONObject doc : docs) {
            result.add(doc.toMap());
        }

        return result;
    }
}
