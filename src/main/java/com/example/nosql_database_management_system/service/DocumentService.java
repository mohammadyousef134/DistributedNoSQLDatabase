package com.example.nosql_database_management_system.service;

import com.example.nosql_database_management_system.DAO.CollectionDAO;
import com.example.nosql_database_management_system.DAO.DocumentDAO;
import com.example.nosql_database_management_system.Schema.SchemaValidator;
import com.example.nosql_database_management_system.model.CollectionSchema;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DocumentService {
    @Autowired
    private DocumentDAO DocDao;
    @Autowired
    private CollectionDAO ColDao;

    // create doc
    public void insertOne(String DBName, String ColName, JSONObject doc) throws IOException {
        CollectionSchema schema = ColDao.getSchemaAsCollectionSchemaObject(DBName, ColName);
        DocDao.insertDoc(DBName, ColName, SchemaValidator.validate(doc, schema));
    }

    // get all
    public JSONArray getALlDocs(String DBName, String ColName) throws IOException{
        return DocDao.getAllDocs(DBName, ColName);
    }

    // get doc
    public Object getDoc(String DBName, String colName, UUID docId) throws IOException{
        Object obj = DocDao.getDoc(DBName, colName, docId).toMap();
        return obj;
    }

    // delete doc
    public void deleteDoc(String DBName, String colName, UUID docId) throws IOException {
        DocDao.deleteDoc(DBName, colName, docId);
    }

    public void updateDoc(String DBName, String colName, UUID docId, String field, String newValue) throws IOException {
        DocDao.updateDoc(DBName, colName, docId, field, newValue);
    }

    public List<Map<String, Object>> filter(String db, String col, String field, String value) {
        return DocDao.filter(db, col, field, value);
    }


}

