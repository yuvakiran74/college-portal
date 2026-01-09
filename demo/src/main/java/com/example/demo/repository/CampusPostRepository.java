package com.example.demo.repository;

import com.example.demo.entity.CampusPost;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CampusPostRepository extends JpaRepository<CampusPost, Long> {
    List<CampusPost> findAllByOrderByIdDesc();
}
