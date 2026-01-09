package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AdminController {

    private final UserRepository repo;

    public AdminController(UserRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        List<User> users = repo.findAll();
        System.out.println("Admin fetching users: " + users.size());
        users.forEach(u -> System.out.println("Found: " + u.getUserId() + " Role: " + u.getRole()));
        return users;
    }

    @PostMapping("/users")
    public ResponseEntity<?> saveUser(@RequestBody User user) {
        try {
            // If it's an update (ID exists), we might want to check existence, but save
            // works for both.
            // Password handling: In a real app we'd hash it. Here we store plain text as
            // per existing pattern.
            User saved = repo.save(user);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error saving user: " + e.getMessage());
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody User user) {
        Optional<User> existing = repo.findById(user.getId());
        if (existing.isPresent()) {
            User u = existing.get();
            u.setPassword(user.getPassword());
            repo.save(u);
            return ResponseEntity.ok("Password updated");
        }
        return ResponseEntity.badRequest().body("User not found");
    }
}
