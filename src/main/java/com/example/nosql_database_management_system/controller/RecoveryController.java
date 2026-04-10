package com.example.nosql_database_management_system.controller;

import com.example.nosql_database_management_system.service.RecoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/internal")
public class RecoveryController {
    @Autowired
    private RecoveryService recoveryService;


    @GetMapping("/exportData")
    public Map<String, Object> exportData() throws IOException {
        return recoveryService.exportAllData();
    }

}
