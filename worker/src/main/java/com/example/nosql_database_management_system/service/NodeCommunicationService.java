package com.example.nosql_database_management_system.service;

import com.example.nosql_database_management_system.model.CollectionSchema;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class NodeCommunicationService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${node.workerPath}")
    private String currentNodePath;
    List<String> nodes = AffinityService.nodes;

    public void forwardInsert(String node, String db, String col, JSONObject doc) {

        String url = node + "/api/insertOne/" + db + "/" + col + "?forwarded=true&replicated=false";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(doc.toMap(), headers);

        restTemplate.postForObject(url, entity, String.class);
    }

    public void forwardUpdate(String node, String db, String col, UUID docId, String field, String value, int expectedVersion) {
        String url = node + "/api/updateDoc/" +
                db + "/" + col + "/" + docId + "/" + field + "/" + value +
                "/" + expectedVersion +
                "?forwarded=true&replicated=false";

        restTemplate.put(url, null, String.class);

    }

    public void forwardDelete(String node, String db, String col, UUID docId) {

        String url = node + "/api/deleteDoc/" +
                db + "/" + col + "/" + docId +
                "?forwarded=true&replicated=false";

        restTemplate.delete(url);
    }

    public void broadcastInsert(String db, String col, JSONObject doc) {
        for (String node : nodes) {
            if (!node.equals(currentNodePath)) {
                String url = node + "/internal/insert/" + db + "/" + col;
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<String> entity =
                        new HttpEntity<>(doc.toString(), headers);

                restTemplate.postForObject(url, entity, String.class);
            }
        }
    }

    public void broadcastUpdate(String db, String col, UUID docId, String field, String value, int newVersion) {
        for (String node : nodes) {
            if (!node.equals(currentNodePath)) {
                String url = node + "/internal/update/" + db + "/" + col + "/" + docId
                        + "?field=" + field
                        + "&value=" + value
                        + "&version=" + newVersion;

                restTemplate.put(url, null, String.class);
            }
        }
    }

    public void broadcastDelete(String db, String col, UUID docId) {
        for (String node : nodes) {
            if (!node.equals(currentNodePath)) {
                String url = node + "/internal/delete/" + db + "/" + col + "/" + docId;
                restTemplate.delete(url);
            }
        }
    }

    // db
    public void broadcastCreateDB(String db) {
        for (String node : nodes) {
            if (!currentNodePath.equals(node)) {
                System.out.println(node);
                String url = node + "/internal/createDB/" + db;
                restTemplate.postForObject(url, null, String.class);
            }
        }
    }

    public void broadcastDeleteDB(String db) {
        for (String node : nodes) {
            if (!node.equals(currentNodePath)) {
                String url = node + "/internal/deleteDB/" + db;
                restTemplate.delete(url);
            }
        }
    }


    // col
    public void broadcastCreateCol(String db, String col, CollectionSchema schema) {
        for (String node : nodes) {
            if (!node.equals(currentNodePath)) {
                String url = node + "/internal/createCol/" + db + "/" + col;
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> entity =
                        new HttpEntity<>(schema.toJSON().toMap(), headers);
                restTemplate.postForObject(url, entity, String.class);
            }
        }
    }

    public void broadcastDeleteCol(String db, String col) {
        for (String node : nodes) {
            if (!node.equals(currentNodePath)) {
                String url = node + "/internal/deleteCol/" + db + "/" + col;
                restTemplate.delete(url);
            }
        }
    }

    public String getOtherNode() {

        for (String node : nodes) {
            if (node.contains(currentNodePath)) continue;
            try {
                restTemplate.getForObject(node + "/internal/exportData", Object.class);
                return node;
            } catch (Exception e) {
                // node is not alive
            }
        }

        return null;
    }

    public Map<String, Object> get(String url) {
        return restTemplate.getForObject(url, Map.class);
    }



}
