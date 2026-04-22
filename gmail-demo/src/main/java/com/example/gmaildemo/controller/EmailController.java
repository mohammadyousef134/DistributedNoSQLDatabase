package com.example.gmaildemo.controller;

import com.example.gmaildemo.DTO.SendEmailRequest;
import com.example.gmaildemo.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/emails")
@CrossOrigin(origins = "http://localhost:3000")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> send(
            @RequestHeader("token") String token,
            @RequestHeader("username") String username,
            @RequestHeader("workerUrl") String workerUrl,
            @RequestBody SendEmailRequest req) {
        req.setFrom(username);
        emailService.sendEmail(workerUrl, token, username, req);
        return ResponseEntity.ok(Map.of("status", "sent"));
    }

    @GetMapping("/inbox")
    public ResponseEntity<?> inbox(
            @RequestHeader("token") String token,
            @RequestHeader("username") String username,
            @RequestHeader("workerUrl") String workerUrl) {
        return ResponseEntity.ok(emailService.getInbox(workerUrl, token, username));
    }

    @GetMapping("/sent")
    public ResponseEntity<?> sent(
            @RequestHeader("token") String token,
            @RequestHeader("username") String username,
            @RequestHeader("workerUrl") String workerUrl) {
        return ResponseEntity.ok(emailService.getSent(workerUrl, token, username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmail(
            @RequestHeader("token") String token,
            @RequestHeader("username") String username,
            @RequestHeader("workerUrl") String workerUrl,
            @PathVariable String id) {
        Map email = emailService.getEmail(workerUrl, token, username, id);
        emailService.markAsRead(workerUrl, token, username, id);
        return ResponseEntity.ok(email);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @RequestHeader("token") String token,
            @RequestHeader("username") String username,
            @RequestHeader("workerUrl") String workerUrl,
            @PathVariable String id) {
        emailService.deleteEmail(workerUrl, token, username, id);
        return ResponseEntity.ok(Map.of("status", "deleted"));
    }
}