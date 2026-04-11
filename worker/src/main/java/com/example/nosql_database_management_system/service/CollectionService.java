package com.example.nosql_database_management_system.service;


import com.example.nosql_database_management_system.DAO.CollectionDAO;
import com.example.nosql_database_management_system.model.CollectionSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CollectionService {
    @Autowired
    CollectionDAO dao;

    @Autowired
    private NodeCommunicationService nodeCommunicationService;

    // create Collection
    public void createCol(String db, String col, CollectionSchema schema, boolean replicated) throws IOException {
        dao.createCol(db, col, schema.toJSON());
        if (!replicated) {
            nodeCommunicationService.broadcastCreateCol(db, col, schema);
        }
    }

    // delete Collection
    public void deleteCol(String db, String col, boolean replicated) {
        dao.deleteCol(db, col);
        if (!replicated) {
            nodeCommunicationService.broadcastDeleteCol(db, col);
        }
    }
}
