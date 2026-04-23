package com.example.gmaildemo.controller;

import com.example.gmaildemo.DTO.SendEmailRequest;
import com.example.gmaildemo.service.EmailService;
import com.example.gmaildemo.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/emails")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://frontend:3000"
})
public class EmailController {

    private final EmailService emailService;
    private final AuthService authService;

    public EmailController(EmailService emailService, AuthService authService) {
        this.emailService = emailService;
        this.authService = authService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> send(
            @RequestHeader("token") String token,
            @RequestHeader("username") String username,
            @RequestBody SendEmailRequest req) {

        String workerUrl = authService.getWorkerForToken(token);

        req.setFrom(username);
        emailService.sendEmail(workerUrl, token, username, req);

        return ResponseEntity.ok(Map.of("status", "sent"));
    }

    @GetMapping("/inbox")
    public ResponseEntity<?> inbox(
            @RequestHeader("token") String token,
            @RequestHeader("username") String username) {
        String workerUrl = authService.getWorkerForToken(token);

        return ResponseEntity.ok(emailService.getInbox(workerUrl, token, username));
    }

    @GetMapping("/sent")
    public ResponseEntity<?> sent(
            @RequestHeader("token") String token,
            @RequestHeader("username") String username) {

        String workerUrl = authService.getWorkerForToken(token);

        return ResponseEntity.ok(emailService.getSent(workerUrl, token, username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmail(
            @RequestHeader("token") String token,
            @RequestHeader("username") String username,
            @PathVariable String id) {

        String workerUrl = authService.getWorkerForToken(token);

        Map email = emailService.getEmail(workerUrl, token, username, id);
        emailService.markAsRead(workerUrl, token, username, id);

        return ResponseEntity.ok(email);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @RequestHeader("token") String token,
            @RequestHeader("username") String username,
            @PathVariable String id) {

        String workerUrl = authService.getWorkerForToken(token);

        emailService.deleteEmail(workerUrl, token, username, id);

        return ResponseEntity.ok(Map.of("status", "deleted"));
    }
}