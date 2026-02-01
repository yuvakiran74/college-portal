package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/faculty")
@CrossOrigin
public class FacultyController {

    private final UserRepository userRepository;

    public FacultyController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/students")
    public List<User> getStudentsBySection(@RequestParam String section) {
        // Assuming "Student" is the role name based on previous findings
        return userRepository.findByRoleAndSection("STUDENT", section);
    }
}
