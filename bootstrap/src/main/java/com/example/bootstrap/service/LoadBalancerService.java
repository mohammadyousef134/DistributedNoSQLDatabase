package com.example.bootstrap.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoadBalancerService {

    private final List<String> workers = List.of(
            "http://worker1:8081",
            "http://worker2:8082"
    );

    private int index = 0;

    public synchronized String getNextWorker() {
        String worker = workers.get(index);
        index++;
        index %= workers.size();
        return worker;
    }
}