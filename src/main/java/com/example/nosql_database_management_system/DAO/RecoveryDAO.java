package com.example.nosql_database_management_system.DAO;

import com.example.nosql_database_management_system.exception.ResourceNotFoundException;
import netscape.javascript.JSObject;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.UUID;


@Repository
public class RecoveryDAO {
    @Autowired
    private DocumentDAO documentDAO;
    public void mergeDocument(String db, String col, JSONObject incoming) throws IOException {
        UUID incomingId = UUID.fromString(incoming.getString("id"));
        JSONObject local = null;
        try {
            local = documentDAO.getDoc(db, col, incomingId);
        }
        catch (Exception e) {
            // not found
//            throw new ResourceNotFoundException("document dose noe exist");
        }

        if (local == null) {
            documentDAO.insertDoc(db, col, incoming);
            return;
        }

        if (incoming.getInt("version") > local.getInt("version")) {
            documentDAO.updateFullDocument(db, col, incomingId, incoming);
        }
    }

}
