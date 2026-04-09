package com.example.nosql_database_management_system.service;

import com.example.nosql_database_management_system.DAO.DatabaseDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatabaseService {
    @Autowired
    private DatabaseDAO dao;
    @Autowired
    private NodeCommunicationService nodeCommunicationService;

    public void createDB(String db, boolean replicated) {
        dao.createDB(db);
        if (!replicated) {
            nodeCommunicationService.broadcastCreateDB(db);
        }
    }

    public void deleteDB(String db, boolean replicated) {
        dao.deleteDB(db);
        if (!replicated) {
            nodeCommunicationService.broadcastDeleteDB(db);
        }
    }

    public List<String> getAllDBs() {
        return dao.getAllDBs();
    }


}
