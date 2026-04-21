package com.example.nosql_database_management_system.DAO;

import com.example.nosql_database_management_system.DAO.indexing.PropertyIndexManager;
import com.example.nosql_database_management_system.cache.LRUCache;
import com.example.nosql_database_management_system.exception.ResourceNotFoundException;
import com.example.nosql_database_management_system.exception.ValidationException;
import com.example.nosql_database_management_system.model.CollectionSchema;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Autowired
    private LRUCache cache;

    @Value("${node.name}")
    private String nodeName;
    private String getBasePath() {
        return "databases_" + nodeName + "/";
    }
    // insert doc
    public void insertDoc(String db, String col, JSONObject doc) throws IOException {
        File file = new File(getBasePath() + db + "/" + col + ".json");
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
        cache.clear();
        indexManager.addToIndex(db, col, doc);
    }

    // get all doc
    public JSONArray getAllDocs(String db, String col) throws IOException {
        File file = new File(getBasePath() + db + "/" + col + ".json");
        if (!file.exists()) {
            throw new ResourceNotFoundException("Database or Collection does not exist");
        }

        String content = Files.readString(file.toPath()).trim();

        if (content.isEmpty()) {
            return new JSONArray();
        }

        return new JSONArray(content);
    }

    // get doc
    public JSONObject getDoc(String db, String col, UUID docId) throws IOException {
        File file = new File(getBasePath() + db + "/" + col + ".json");
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
        File file = new File(getBasePath() + db + "/" + col + ".json");
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
        cache.clear();
        indexManager.removeFromIndex(db, col, object);
    }

    public int updateDocWithVersion(String db, String col, UUID docId, String field, String newValue, int expectedVersion) throws IOException {
        File file = new File(getBasePath() + db + "/" + col + ".json");

        if (!file.exists()) {
            throw new ResourceNotFoundException("Database or Collection does not exist");
        }

        String content = Files.readString(file.toPath());
        JSONArray array = new JSONArray(content);
        JSONArray newArray = new JSONArray();
        boolean found = false;
        int newVersion = -1;
        JSONObject oldDoc = new JSONObject();
        JSONObject newDoc = new JSONObject();

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            if (obj.getString("id").equals(docId.toString())) {
                found = true;
                oldDoc = obj.similar(obj) ? new JSONObject(obj.toString()) : obj; // snapshot before mutation

                int currentVersion = obj.optInt("version", 1);
                if (currentVersion != expectedVersion) {
                    throw new ValidationException("Version mismatch");
                }

                CollectionSchema schema = collectionDAO.getSchemaAsCollectionSchemaObject(db, col);
                if (!schema.getField(field).isNullable() && newValue == null) {
                    throw new ValidationException("Null not allowed");
                }

                Object parsedValue = field.equals("read") ? Boolean.parseBoolean(newValue) : newValue;
                obj.put(field, parsedValue);

                newVersion = currentVersion + 1;
                obj.put("version", newVersion);

                newDoc = obj;
            }
            newArray.put(obj);
        }

        if (!found) {
            throw new ResourceNotFoundException("Document does not exist");
        }

        try (FileWriter fileWriter = new FileWriter(file, false)) {
            fileWriter.write(newArray.toString(4));
        }
        if (oldDoc != null) indexManager.removeFromIndex(db, col, oldDoc);
        indexManager.addToIndex(db, col, newDoc);

        cache.clear();
        return newVersion;

    }

    public List<Map<String, Object>> filter(String db, String col, String field, String value) {

        List<JSONObject> docs = indexManager.search(db, col, field, value);

        List<Map<String, Object>> result = new ArrayList<>();

        for (JSONObject doc : docs) {
            result.add(doc.toMap());
        }

        return result;
    }

    public void applyReplicatedUpdate(String db, String col, UUID docId, String field, String value, int version) throws IOException {
        JSONObject localDoc = getDoc(db, col, docId);

        int localVersion = localDoc.optInt("version", 1);

        // coming < local
        if (version <= localVersion) {
            return;
        }

        localDoc.put(field, value);
        localDoc.put("version", version);

        updateFullDocument(db, col, docId, localDoc);
    }
    public void updateFullDocument(String db, String col, UUID docId, JSONObject doc) throws IOException {
        File file = new File(getBasePath() + db + "/" + col + ".json");
        String content = Files.readString(file.toPath());
        JSONArray array = new JSONArray(content);
        JSONArray newArray = new JSONArray();
        JSONObject oldDoc = new JSONObject();
        boolean found = false;
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            if (obj.getString("id").equals(docId.toString())) {
                found = true;
                newArray.put(doc);
                oldDoc = obj.similar(obj) ? new JSONObject(obj.toString()) : obj;
                continue;
            }
            newArray.put(obj);
        }
        if (!found) {
            throw new ResourceNotFoundException("Document does not exist");
        }
        try (FileWriter fileWriter = new FileWriter(file, false)) {
            fileWriter.write(newArray.toString(4));
        }
        if (oldDoc != null) indexManager.removeFromIndex(db, col, oldDoc);
        indexManager.addToIndex(db, col, doc);
        cache.clear();
    }

}
