package com.example.nosql_database_management_system.DAO.indexing;

import com.example.nosql_database_management_system.DAO.DocumentDAO;
import jakarta.annotation.PostConstruct;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Component
public class IndexInitializer {
    @Autowired
    private DocumentDAO documentDAO;
    @Autowired
    private PropertyIndexManager indexManager;

    @Value("${node.name}")
    private String nodeName;
    private String getBasePath() {
        return "databases_" + nodeName + "/";
    }

    @PostConstruct
    public void init() throws IOException {
        rebuildIndex();
    }


    private void rebuildIndex() throws IOException {
        File base = new File(getBasePath());

        File[] files = base.listFiles();
        if (files == null) return;
        for (File db : files) {
            if (!db.isDirectory()) continue;

            String dbName = db.getName();

            for (File colFile : db.listFiles()) {
                if (!colFile.getName().endsWith(".json")) continue;

                String colName = colFile.getName().replace(".json", "");

                String content = new String(Files.readAllBytes(colFile.toPath()));

                if (content.isEmpty()) {
                    continue;
                }
                JSONArray docs = new JSONArray(content);

                for (int i = 0; i < docs.length(); i++) {
                    JSONObject doc = docs.getJSONObject(i);

                    indexManager.addToIndex(dbName, colName, doc);
                }
            }
        }
    }
}