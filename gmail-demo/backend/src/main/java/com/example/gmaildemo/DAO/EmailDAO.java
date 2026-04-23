package com.example.gmaildemo.DAO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Repository
public class EmailDAO {

    private final RestTemplate restTemplate;
    private final String dbName;

    public EmailDAO(RestTemplate restTemplate,
                    @Value("${db.name}") String dbName) {
        this.restTemplate = restTemplate;
        this.dbName = dbName;
    }

    public void insertDocument(String workerUrl, String token, String username,
                               String col, Map<String, Object> doc) {
        String url = workerUrl + "/api/insertOne/" + dbName + "/" + col;
        HttpEntity<Map> request = new HttpEntity<>(doc, buildHeaders(token, username));
        System.out.println("WORKER URL = " + workerUrl);
        restTemplate.postForEntity(url, request, Map.class);
    }



    public List<Map<String, Object>> getAllDocuments(String workerUrl, String token,
                                                     String username, String col) {
        String url = workerUrl + "/api/getAllDocs/" + dbName + "/" + col;
        HttpEntity<?> request = new HttpEntity<>(buildHeaders(token, username));
        ResponseEntity<List> response = restTemplate.exchange(
                url, HttpMethod.GET, request, List.class);
        return response.getBody();
    }

    public Map getDocumentById(String workerUrl, String token, String username,
                               String col, String docId) {
        String url = workerUrl + "/api/getDoc/" + dbName + "/" + col + "/" + docId;
        HttpEntity<?> request = new HttpEntity<>(buildHeaders(token, username));
        ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, request, Map.class);
        return response.getBody();
    }

    public void updateField(String workerUrl, String token, String username,
                            String col, String docId, String field,
                            String value, int version) {
        String url = workerUrl + "/api/updateDoc/" + dbName + "/" + col
                + "/" + docId + "/" + field + "/" + value.toString() + "/" + version;
        System.out.println(url);
        HttpEntity<?> request = new HttpEntity<>(buildHeaders(token, username));
        restTemplate.exchange(url, HttpMethod.PUT, request, Map.class);
    }

    public List<Map<String, Object>> filter(String workerUrl, String token,
                                            String username,
                                            String collection,
                                            String field,
                                            String value) {

        String url = workerUrl + "/api/filter/emaildb/" + collection + "/" + field + "/" + value;
        HttpHeaders headers = new HttpHeaders();
        headers.set("username", username);
        headers.set("token", token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                List.class
        );

        return response.getBody();
    }

    public void deleteDocument(String workerUrl, String token, String username,
                               String col, String docId) {
        String url = workerUrl + "/api/deleteDoc/" + dbName + "/" + col + "/" + docId;
        HttpEntity<?> request = new HttpEntity<>(buildHeaders(token, username));
        restTemplate.exchange(url, HttpMethod.DELETE, request, Map.class);
    }

    private HttpHeaders buildHeaders(String token, String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("token", token);
        headers.set("username", username);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}