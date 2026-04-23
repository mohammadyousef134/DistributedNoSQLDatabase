package com.example.gmaildemo.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Repository
public class AuthDAO {
    @Autowired
    private RestTemplate restTemplate;

    private String dbBaseUrl;

    public AuthDAO(@Value("${db.bootstrap.url}") String dbBaseUrl) {
        this.dbBaseUrl = dbBaseUrl;
    }
    public Map register(String username) {
        System.out.println(dbBaseUrl);
        String url = dbBaseUrl + "/api/register/" + username;
        ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);
        return response.getBody();

    }
}
