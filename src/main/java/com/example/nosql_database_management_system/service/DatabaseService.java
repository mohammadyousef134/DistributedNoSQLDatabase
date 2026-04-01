package com.example.nosql_database_management_system.service;

import com.example.nosql_database_management_system.DAO.DatabaseDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatabaseService {
    @Autowired
    private DatabaseDAO dao;

    public void createDB(String name) {
        dao.createDB(name);
    }

    public void deleteDB(String name) {
        dao.deleteDB(name);
    }

    public List<String> getAllDBs() {
        return dao.getAllDBs();
    }


}
