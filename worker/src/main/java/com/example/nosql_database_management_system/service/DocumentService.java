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
    public void insertOne(String db, String col, JSONObject doc, boolean forwarded, boolean replicated) throws IOException {
        CollectionSchema schema = ColDao.getSchemaAsCollectionSchemaObject(db, col);
        JSONObject validatedDoc;
        if (!doc.has("id")) {
            validatedDoc = SchemaValidator.validate(doc, schema); // has id
        }
        else {
            validatedDoc = doc;
        }

        String docId = validatedDoc.get("id").toString();
        String affinityNode = affinityService.getAffinityNode(docId);
        // if doc hasId no validate

        if (!forwarded && !currentNode.equals(affinityNode)) {

            nodeCommunicationService.forwardInsert(affinityNode, db, col, validatedDoc);
            return;
        }

        DocDao.insertDoc(db, col, validatedDoc);

        if (!replicated) {
            nodeCommunicationService.broadcastInsert(db, col, validatedDoc);
        }
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
    public void deleteDoc(String db, String col, UUID docId, boolean forwarded, boolean replicated) throws IOException {
        String affinityNode = affinityService.getAffinityNode(docId.toString());

        if (!forwarded && !currentNode.equals(affinityNode)) {
            nodeCommunicationService.forwardDelete(affinityNode, db, col, docId);
            return;
        }

        DocDao.deleteDoc(db, col, docId);

        if (!replicated) {
            nodeCommunicationService.broadcastDelete(db, col, docId);
        }
    }

    public void updateDoc(String db, String col, UUID docId, String field, String newValue, int version, boolean forwarded, boolean replicated) throws IOException {
        String affinityNode = affinityService.getAffinityNode(docId.toString());

        if (!forwarded && !currentNode.equals(affinityNode)) {
            nodeCommunicationService.forwardUpdate(affinityNode, db, col, docId, field, newValue, version);
            return;
        }
        if (replicated) {
            DocDao.applyReplicatedUpdate(db, col, docId, field, newValue, version);
            return;
        }
        int newVersion = DocDao.updateDocWithVersion(db, col, docId, field, newValue, version);
        nodeCommunicationService.broadcastUpdate(db, col, docId, field, newValue, newVersion);
    }

    public List<Map<String, Object>> filter(String db, String col, String field, String value) {
        return DocDao.filter(db, col, field, value);
    }


}

