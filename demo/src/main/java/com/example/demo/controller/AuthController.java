package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    private final UserRepository repo;

    public AuthController(UserRepository repo) {
        this.repo = repo;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            if (repo.findByUserIdAndPassword(user.getUserId(), user.getPassword()).isPresent()) {
                return ResponseEntity.badRequest().body("User ID already exists");
            }

            // Validate One Class Teacher per Section
            if ("FACULTY".equals(user.getRole()) && user.getSection() != null) {
                if (repo.existsByRoleAndSection("FACULTY", user.getSection())) {
                    return ResponseEntity.badRequest()
                            .body("Section " + user.getSection() + " already has a Class Teacher.");
                }
            }
            User savedUser = repo.save(user);
            System.out.println("Registered User: " + savedUser.getUserId() + " Role: " + savedUser.getRole());
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public User login(@RequestBody User user) {
        return repo.findByUserIdAndPassword(
                user.getUserId(),
                user.getPassword()).orElse(null);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> payload) {
        String userId = payload.get("userId");
        String currentPassword = payload.get("currentPassword");
        String newPassword = payload.get("newPassword");

        if (userId == null || currentPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body("Missing required parameters.");
        }

        Optional<User> userOpt = repo.findByUserIdAndPassword(userId, currentPassword);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(newPassword);
            repo.save(user);
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.badRequest().body("Incorrect current password.");
        }
    }
}
