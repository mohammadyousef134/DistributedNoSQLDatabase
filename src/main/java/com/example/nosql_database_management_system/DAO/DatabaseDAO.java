package com.example.nosql_database_management_system.DAO;

import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Repository
public class DatabaseDAO {
    private final String BASE_PATH = "databases/";


    public void createDB(String name) {
        File DBpath = new File(BASE_PATH + name);

        if (!DBpath.exists()) {
            DBpath.mkdir();

            File schemaPath = new File(BASE_PATH + name + "/schemas");
            schemaPath.mkdir();
        }
    }

    public void deleteDB(String name) {
        File file = new File(BASE_PATH + name);
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

    public List<String> listDBs() {
        File folder = new File(BASE_PATH);
        String[] names = folder.list();

        if (names == null) return List.of();
        return Arrays.asList(names);
    }
}
