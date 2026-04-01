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

    // create Collection
    public void createCol(String DBName, String ColName, CollectionSchema schema) throws IOException {
        dao.createCol(DBName, ColName, schema.toJSON());
    }

    // delete Collection
    public void deleteCol(String DBName, String CollectionName) {
        dao.deleteCol(DBName, CollectionName);
    }
}
