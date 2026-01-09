package com.example.demo.controller;

import com.example.demo.entity.CampusPost;
import com.example.demo.service.CampusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/campus")
@CrossOrigin
public class CampusController {

    private final CampusService service;

    public CampusController(CampusService service) {
        this.service = service;
    }

    @PostMapping("/post")
    public CampusPost createPost(@RequestBody CampusPost post) {
        System.out.println("Received Post: " + post.getTitle());
        return service.createPost(post);
    }

    @GetMapping("/all")
    public List<CampusPost> getAllPosts() {
        return service.getAllPosts();
    }
}
