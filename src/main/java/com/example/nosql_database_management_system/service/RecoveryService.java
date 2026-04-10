package com.example.nosql_database_management_system.service;

import com.example.nosql_database_management_system.DAO.RecoveryDAO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecoveryService {

    @Autowired
    private RecoveryDAO recoveryDAO;

    @Autowired
    private NodeCommunicationService nodeCommunicationService;

    @Value("${node.name}")
    private String nodeName;

    private String getBasePath() {
        return "databases_" + nodeName + "/";
    }

    @PostConstruct
    public void recoverIfNeeded() {
        try {
            String node = nodeCommunicationService.getOtherNode();
            if (node == null) {
                System.out.println("No other node available for recovery");
                return;
            }

            Map<String, Object> data =
                    nodeCommunicationService.get(node + "/internal/exportData");

            if (data == null || !data.containsKey("databases")) {
                System.out.println("No data received for recovery");
                return;
            }

            mergeData(data);

            System.out.println("Recovery completed successfully");

        } catch (Exception e) {
            System.out.println("Recovery skipped: " + e.getMessage());
        }
    }

    public void mergeData(Map<String, Object> data) throws IOException {

        Map<String, Object> databases =
                (Map<String, Object>) data.get("databases");

        for (String dbName : databases.keySet()) {

            Map<String, Object> collections =
                    (Map<String, Object>) databases.get(dbName);

            for (String colName : collections.keySet()) {

                List<Map<String, Object>> docs =
                        (List<Map<String, Object>>) collections.get(colName);

                for (Map<String, Object> docMap : docs) {
                    JSONObject doc = new JSONObject(docMap);
                    recoveryDAO.mergeDocument(dbName, colName, doc);
                }
            }
        }
    }
    public Map<String, Object> exportAllData() throws IOException {

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> databasesMap = new HashMap<>();

        File baseDir = new File(getBasePath());

        if (!baseDir.exists()) {
            result.put("databases", databasesMap);
            return result;
        }

        for (File dbDir : baseDir.listFiles()) {

            if (!dbDir.isDirectory()) continue;

            String dbName = dbDir.getName();
            Map<String, Object> collectionsMap = new HashMap<>();

            for (File file : dbDir.listFiles()) {
                if (file.isDirectory()) continue;
                if (!file.getName().endsWith(".json")) continue;

                String colName = file.getName().replace(".json", "");

                String content = Files.readString(file.toPath());

                JSONArray array = content.isEmpty() ? new JSONArray() : new JSONArray(content);

                List<Map<String, Object>> docs = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    docs.add(array.getJSONObject(i).toMap());
                }

                collectionsMap.put(colName, docs);
            }

            databasesMap.put(dbName, collectionsMap);
        }

        result.put("databases", databasesMap);
        return result;
    }
}