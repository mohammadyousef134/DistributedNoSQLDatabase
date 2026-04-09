package com.example.nosql_database_management_system.service;

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
import java.util.logging.Handler;

@Service
public class NodeCommunicationService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${node.workerPath}")
    private String currentNode;
    List<String> nodes = AffinityService.nodes;

    public void forwardInsert(String node, String db, String col, JSONObject doc) {

        String url = node + "/api/insertOne/" + db + "/" + col + "?forwarded=true&replicated=false";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(doc.toMap(), headers);

        restTemplate.postForObject(url, entity, String.class);
    }

    public void forwardUpdate(String node, String db, String col, UUID docId, String field, String value) {
        String url = node + "/api/updateDoc/" +
                db + "/" + col + "/" + docId + "/" + field + "/" + value +
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
            if (!node.equals(currentNode)) {
                String url = node + "/api/insertOne/" + db + "/" + col + "?forwarded=true&replicated=true";
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Map<String, Object>> entity =
                        new HttpEntity<>(doc.toMap(), headers);

                restTemplate.postForObject(url, entity, String.class);
            }
        }
    }

    public void broadcastUpdate(String db, String col, UUID docId, String field, String value) {
        for (String node : nodes) {
            if (!node.equals(currentNode)) {
                String url = node + "/api/updateDoc/" + db + "/" + col + "/" + docId + "/" + field +
                        "/" + value + "?forwarded=true&replicated=true";

                restTemplate.put(url, null, String.class);
            }
        }
    }

    public void broadcastDelete(String db, String col, UUID docId) {
        for (String node : nodes) {
            if (!node.equals(currentNode)) {
                String url = node + "/api/deleteDoc/" + db + "/" + col + "/" + docId + "?forwarded=true&replicated=true";
                restTemplate.delete(url);
            }
        }
    }
}
