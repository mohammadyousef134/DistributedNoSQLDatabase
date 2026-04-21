package com.example.nosql_database_management_system.DAO.indexing;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PropertyIndexManager {
    private final Map<PropertyIndex, List<JSONObject>> index = new ConcurrentHashMap<>();

    public void addToIndex(String db, String col, JSONObject doc) {
        JSONObject snapshot = new JSONObject(doc.toString()); //  deep copy — freeze the state at insert time

        for (String key : snapshot.keySet()) {
            Object valueObj = snapshot.get(key);
            if (valueObj == null) continue;

            String value = valueObj.toString();
            PropertyIndex indexKey = new PropertyIndex(db, col, key, value);
            index.computeIfAbsent(indexKey, k -> new ArrayList<>()).add(snapshot);
        }
    }

    public List<JSONObject> search(String db, String col, String field, String value) {

        PropertyIndex key = new PropertyIndex(db, col, field, value);

        return index.getOrDefault(key, new ArrayList<>());
    }

    public void removeFromIndex(String db, String col, JSONObject doc) {
        String docId = doc.optString("id");

        for (String key : doc.keySet()) {
            Object valueObj = doc.get(key);
            if (valueObj == null) continue;

            String value = valueObj.toString();
            PropertyIndex indexKey = new PropertyIndex(db, col, key, value);

            List<JSONObject> list = index.get(indexKey);
            if (list != null) {
                list.removeIf(entry -> docId.equals(entry.optString("id")));
                if (list.isEmpty()) {
                    index.remove(indexKey);
                }
            }
        }
    }
}
