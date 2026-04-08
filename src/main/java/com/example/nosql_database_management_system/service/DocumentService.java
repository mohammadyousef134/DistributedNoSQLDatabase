package com.example.nosql_database_management_system.service;

import com.example.nosql_database_management_system.DAO.CollectionDAO;
import com.example.nosql_database_management_system.DAO.DocumentDAO;
import com.example.nosql_database_management_system.Schema.SchemaValidator;
import com.example.nosql_database_management_system.model.CollectionSchema;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    @Autowired
    private AffinityService affinityService;
    @Autowired
    private NodeCommunicationService nodeCommunicationService;

    @Value("${node.workerPath}")
    private String currentNode;

    // insert doc
    public void insertOne(String db, String col, JSONObject doc, boolean forwarded) throws IOException {
        CollectionSchema schema = ColDao.getSchemaAsCollectionSchemaObject(db, col);
        JSONObject validatedDoc = SchemaValidator.validate(doc, schema);

        String docId = validatedDoc.get("id").toString();
        String affinityNode = affinityService.getAffinityNode(docId);
        System.out.println("Current Node: " + currentNode);
        System.out.println("Affinity Node: " + affinityNode);

        if (!forwarded && !currentNode.equals(affinityNode)) {
            System.out.println("Forwarding delete to: " + affinityNode);

            nodeCommunicationService.forwardInsert(affinityNode, db, col, doc);
            return;
        }
        DocDao.insertDoc(db, col, validatedDoc);
    }

    // get all
    public JSONArray getALlDocs(String db, String col) throws IOException{
        return DocDao.getAllDocs(db, col);
    }

    // get doc
    public Object getDoc(String db, String col, UUID docId) throws IOException{
        Object obj = DocDao.getDoc(db, col, docId).toMap();
        return obj;
    }

    // delete doc
    public void deleteDoc(String db, String col, UUID docId, boolean forwarded) throws IOException {
        String affinityNode = affinityService.getAffinityNode(docId.toString());

        System.out.println("Current Node: " + currentNode);
        System.out.println("Affinity Node: " + affinityNode);

        if (!forwarded && !currentNode.equals(affinityNode)) {
            System.out.println("Forwarding delete to: " + affinityNode);

            nodeCommunicationService.forwardDelete(affinityNode, db, col, docId);
            return;
        }
        DocDao.deleteDoc(db, col, docId);
    }

    public void updateDoc(String db, String col, UUID docId, String field, String newValue, boolean forwarded) throws IOException {
        String affinityNode = affinityService.getAffinityNode(docId.toString());

        System.out.println("Current Node: " + currentNode);
        System.out.println("Affinity Node: " + affinityNode);

        if (!forwarded && !currentNode.equals(affinityNode)) {
            System.out.println("Forwarding delete to: " + affinityNode);

            nodeCommunicationService.forwardUpdate(affinityNode, db, col, docId, field, newValue);
            return;
        }
        DocDao.updateDoc(db, col, docId, field, newValue);
    }

    public List<Map<String, Object>> filter(String db, String col, String field, String value) {
        return DocDao.filter(db, col, field, value);
    }


}

