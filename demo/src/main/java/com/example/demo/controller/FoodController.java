package com.example.demo.controller;

import com.example.demo.entity.FoodOrder;
import com.example.demo.repository.FoodOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/food")
public class FoodController {

    @Autowired
    private FoodOrderRepository foodOrderRepository;

    @PostMapping("/order")
    public ResponseEntity<?> placeOrder(@RequestBody Map<String, Object> payload) {
        try {
            String studentId = (String) payload.get("studentId");
            String items = (String) payload.get("items");
            Double total = Double.valueOf(payload.get("total").toString());

            // Generate unique Order ID
            String uniqueId = "FOOD-" + (int) (Math.random() * 9000 + 1000);

            // Ensure uniqueness (simple retry)
            while (foodOrderRepository.findByOrderId(uniqueId).isPresent()) {
                uniqueId = "FOOD-" + (int) (Math.random() * 9000 + 1000);
            }

            FoodOrder order = new FoodOrder();
            order.setOrderId(uniqueId);
            order.setStudentId(studentId);
            order.setItems(items);
            order.setTotalAmount(total);
            order.setOrderTime(LocalDateTime.now());
            order.setStatus("PENDING");

            foodOrderRepository.save(order);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Order Placed Successfully");
            response.put("orderId", uniqueId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/history/{studentId}")
    public ResponseEntity<List<FoodOrder>> getHistory(@PathVariable String studentId) {
        return ResponseEntity.ok(foodOrderRepository.findByStudentIdOrderByOrderTimeDesc(studentId));
    }

    @PostMapping("/verify/{orderId}")
    public ResponseEntity<?> verifyOrder(@PathVariable String orderId) {
        Optional<FoodOrder> orderOpt = foodOrderRepository.findByOrderId(orderId);

        if (orderOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("status", "INVALID", "message", "Invalid Order ID"));
        }

        FoodOrder order = orderOpt.get();
        if ("COMPLETED".equalsIgnoreCase(order.getStatus())) {
            return ResponseEntity.ok(Map.of(
                    "status", "ALREADY_COMPLETED",
                    "message", "Order #" + orderId + " was already served.",
                    "studentId", order.getStudentId(),
                    "items", order.getItems()));
        }

        // Mark as Completed
        order.setStatus("COMPLETED");
        foodOrderRepository.save(order);

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Order #" + orderId + " is Valid. Marked as Served!",
                "studentId", order.getStudentId(),
                "items", order.getItems()));
    }
}
