package com.example.demo.repository;

import com.example.demo.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    java.util.List<FoodItem> findByIsDefaultTrue();
}
