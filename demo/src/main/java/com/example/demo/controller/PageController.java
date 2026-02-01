package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/login")
    public String loginPage() {
        return "redirect:/login.html";
    }

    @GetMapping("/student")
    public String studentPage() {
        return "redirect:/student.html";
    }

    @GetMapping("/faculty")
    public String facultyPage() {
        return "redirect:/faculty.html";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "redirect:/login.html";
    }

    @GetMapping("/")
    public String rootPage() {
        return "redirect:/login.html";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "redirect:/admin_dashboard.html";
    }
}
