package com.example.gmaildemo.service;

import com.example.gmaildemo.DAO.EmailDAO;
import com.example.gmaildemo.DTO.SendEmailRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmailService {

    private final EmailDAO emailDAO;

    public EmailService(EmailDAO emailDAO) {
        this.emailDAO = emailDAO;
    }

    // done
    public void sendEmail(String workerUrl, String token,
                          String username, SendEmailRequest req) {
        long now = System.currentTimeMillis();

        Map<String, Object> emailDoc = new HashMap<>();
        emailDoc.put("from", req.getFrom());
        emailDoc.put("to", req.getTo());
        emailDoc.put("subject", req.getSubject());
        emailDoc.put("body", req.getBody());
        emailDoc.put("timestamp", now);
        emailDoc.put("read", false);

        emailDAO.insertDocument(workerUrl, token, username, "emails", emailDoc);
    }

    // done
    public List<Map<String, Object>> getInbox(String workerUrl, String token, String username) {
        List<Map<String, Object>> inbox =
                emailDAO.filter(workerUrl, token, username, "emails", "to", username);

        if (inbox == null) {
            return List.of();
        }

        inbox.sort((a, b) ->
                Long.compare(toLong(b.get("timestamp")), toLong(a.get("timestamp")))
        );

        return inbox;
    }

    //done
    public List<Map<String, Object>> getSent(String workerUrl, String token, String username) {
        List<Map<String, Object>> sent =
                emailDAO.filter(workerUrl, token, username, "emails", "from", username);

        if (sent == null) return List.of();
        sent.sort((a, b) -> Long.compare(toLong(b.get("timestamp")), toLong(a.get("timestamp"))));
        return sent;
    }

    // done
    public Map<String, Object> getEmail(String workerUrl, String token, String username, String emailId) {
        return emailDAO.getDocumentById(workerUrl, token, username, "emails", emailId);
    }

    // done
    public void markAsRead(String workerUrl, String token, String username, String emailId) {
        Map email = getEmail(workerUrl, token, username, emailId);
        System.out.println(email.get("version"));
        int version = toInt(email.get("version"));
        if (version >= 2) return;
        emailDAO.updateField(workerUrl, token, username, "emails", emailId, "read", "true", version);
    }

    // done
    public void deleteEmail(String workerUrl, String token, String username, String emailId) {
        emailDAO.deleteDocument(workerUrl, token, username, "emails", emailId);
    }
    private long toLong(Object val) {
        if (val == null) return 0L;
        if (val instanceof Number) return ((Number) val).longValue();
        return Long.parseLong(val.toString());
    }

    private int toInt(Object val) {
        if (val == null) return 1;
        if (val instanceof Number) return ((Number) val).intValue();
        return Integer.parseInt(val.toString());
    }
}