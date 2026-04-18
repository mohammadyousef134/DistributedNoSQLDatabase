package com.example.nosql_database_management_system.cache;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class LRUCache {

    private final int MAX_SIZE = 100;

    private final Map<String, Object> cache = new LinkedHashMap<>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
            return size() > MAX_SIZE;
        }
    };

    public synchronized Object get(String key) {
        return cache.get(key);
    }

    public synchronized void put(String key, Object value) {
        cache.put(key, value);
    }

    public synchronized void clear() {
        cache.clear();
    }
}