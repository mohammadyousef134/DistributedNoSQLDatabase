package com.example.nosql_database_management_system.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AffinityService {
    List<String> nodes = List.of(
            "http://localhost:8081",
            "http://localhost:8082"
    );
    public String getAffinityNode(String documentId) {
        int hash = Math.abs(documentId.hashCode());
        int idx = hash % nodes.size();
        return nodes.get(idx);
    }
}
