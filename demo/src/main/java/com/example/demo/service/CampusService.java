package com.example.demo.service;

import com.example.demo.entity.CampusPost;
import com.example.demo.repository.CampusPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampusService {

    @Autowired
    private CampusPostRepository repository;

    public CampusPost createPost(CampusPost post) {
        return repository.save(post);
    }

    public List<CampusPost> getAllPosts() {
        return repository.findAllByOrderByIdDesc();
    }
}
