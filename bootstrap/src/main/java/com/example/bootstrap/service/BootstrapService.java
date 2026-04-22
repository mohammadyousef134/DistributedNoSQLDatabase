package com.example.bootstrap.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class BootstrapService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private LoadBalancerService loadBalancer;

    @Autowired
    private RestTemplate restTemplate;

    public Map<String, String> registerUser(String username) {
        String workerUrl = loadBalancer.getNextWorker();

        String workerName = extractWorkerName(workerUrl);

        String token = tokenService.generateToken(workerName);

        String url = workerUrl + "/internal/addAuthenticatedUser/"
                 + username + "/" + token + "/" + workerName;

        restTemplate.postForObject(url, null, String.class);

        return Map.of(
                "username", username,
                "token", token,
                "worker", workerUrl
        );
    }

    private String extractWorkerName(String url) {
        if (url.contains("8081")) return "worker1";
        if (url.contains("8082")) return "worker2";
        return "worker3";
    }
}