package com.example.nosql_database_management_system.DAO;

import com.example.nosql_database_management_system.exception.ForbiddenException;
import com.example.nosql_database_management_system.exception.ResourceNotFoundException;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Repository
public class DatabaseDAO {
    private final String BASE_PATH = "databases/";

    public DatabaseDAO() {
        File base = new File(BASE_PATH);
        if (!base.exists()) {
            base.mkdir();
        }
    }
    public void createDB(String name) {
        File DBpath = new File(BASE_PATH + name);
        if (!DBpath.exists()) {
            DBpath.mkdir();

            File schemaPath = new File(BASE_PATH + name + "/schemas");
            schemaPath.mkdir();
        }
        else {
            throw new ForbiddenException("database is already existed");
        }
    }

    public void deleteDB(String name) {
        File file = new File(BASE_PATH + name);
        if (!file.exists()) {
            throw new ResourceNotFoundException("Database not found: " + name);
        }
        deleteDirectory(file);
    }

    private void deleteDirectory(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteDirectory(f);
                }
            }
        }
        file.delete();
    }

    public List<String> getAllDBs() {
        File folder = new File(BASE_PATH);
        String[] names = folder.list();

        if (names == null) return List.of();
        return Arrays.asList(names);
    }
}
