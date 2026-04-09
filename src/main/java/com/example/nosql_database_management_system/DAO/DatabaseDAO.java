package com.example.nosql_database_management_system.DAO;

import com.example.nosql_database_management_system.exception.ForbiddenException;
import com.example.nosql_database_management_system.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Repository
public class DatabaseDAO {

    @Value("${node.name}")
    private String nodeName;
    private String getBasePath() {
        return "databases_" + nodeName + "/";
    }

    public DatabaseDAO() {
        File base = new File(getBasePath());
        if (!base.exists()) {
            base.mkdirs();
        }
        System.out.println(nodeName);
        System.out.println(base.getAbsolutePath());

    }
    public void createDB(String name) {
        File DBpath = new File(getBasePath() + name);
        if (!DBpath.exists()) {
            DBpath.mkdirs();

            File schemaPath = new File(getBasePath() + name + "/schemas");
            schemaPath.mkdirs();
        }
        else {
            throw new ForbiddenException("database is already existed");
        }
    }

    public void deleteDB(String name) {
        File file = new File(getBasePath() + name);
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
        File folder = new File(getBasePath());
        String[] names = folder.list();

        if (names == null) return List.of();
        return Arrays.asList(names);
    }
}
