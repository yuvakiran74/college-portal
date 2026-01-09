package com.example.demo.repository;

import com.example.demo.entity.FoodOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FoodOrderRepository extends JpaRepository<FoodOrder, Long> {
    Optional<FoodOrder> findByOrderId(String orderId);

    List<FoodOrder> findByStudentIdOrderByOrderTimeDesc(String studentId);
}
