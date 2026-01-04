package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            User savedUser = repo.save(user);
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
}
