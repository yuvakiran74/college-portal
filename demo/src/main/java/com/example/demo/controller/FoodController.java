package com.example.demo.controller;

import com.example.demo.entity.DailyMenu;
import com.example.demo.entity.FoodItem;
import com.example.demo.entity.FoodOrder;
import com.example.demo.repository.DailyMenuRepository;
import com.example.demo.repository.FoodItemRepository;
import com.example.demo.repository.FoodOrderRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/food")
@CrossOrigin("*")
public class FoodController {

    @Autowired
    private FoodOrderRepository foodOrderRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private DailyMenuRepository dailyMenuRepository;

    @PostConstruct
    public void init() {
        if (foodItemRepository.count() == 0) {
            List<FoodItem> items = Arrays.asList(
                    new FoodItem("Meals", 80.0, "🍲"),
                    new FoodItem("Parota (2pcs)", 50.0, "🫓"),
                    new FoodItem("Chapathi (2pcs)", 40.0, "🐢"),
                    new FoodItem("Chicken Biryani", 150.0, "🍗"),
                    new FoodItem("Veg Biryani", 100.0, "🥗"),
                    new FoodItem("Fried Rice", 90.0, "🍚"),
                    new FoodItem("Cold Coffee", 30.0, "🥤"));
            foodItemRepository.saveAll(items);
        }

    }

    @PostMapping("/master-item")
    public ResponseEntity<?> addFoodItem(@RequestBody FoodItem item) {
        System.out.println("Received Request to Add Item: " + item.getName());
        item.setDefault(false); // New items are NOT default globally
        FoodItem saved = foodItemRepository.save(item);
        return ResponseEntity.ok(Map.of("message", "Item Added Successfully", "id", saved.getId()));
    }

    @GetMapping("/daily-menu")
    public ResponseEntity<List<Map<String, Object>>> getMenu(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        System.out.println("Fetching Menu for: " + date);
        List<DailyMenu> dailyMenus = dailyMenuRepository.findByDate(date);

        if (dailyMenus.isEmpty()) {
            // No menu published? Return empty (Holiday/Not Planned)
            return ResponseEntity.ok(Collections.emptyList());
        }

        // Return persisted Daily Menu
        return ResponseEntity.ok(dailyMenus.stream().map(dm -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", dm.getFoodItem().getId());
            map.put("name", dm.getFoodItem().getName());
            map.put("icon", dm.getFoodItem().getIcon());
            map.put("price", dm.getPrice());
            map.put("available", dm.getQuantity());
            map.put("isPublished", true);
            map.put("dailyMenuId", dm.getId());
            return map;
        }).collect(Collectors.toList()));
    }

    @PostMapping("/daily-menu/init")
    public ResponseEntity<?> initDailyMenu(@RequestBody Map<String, String> payload) {
        String dateStr = payload.get("date");
        LocalDate date = LocalDate.parse(dateStr);

        if (!dailyMenuRepository.findByDate(date).isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Menu already exists for this date"));
        }

        List<FoodItem> defaults = foodItemRepository.findByIsDefaultTrue();
        List<DailyMenu> newMenus = defaults.stream().map(item -> {
            DailyMenu dm = new DailyMenu();
            dm.setDate(date);
            dm.setFoodItem(item);
            dm.setPrice(item.getDefaultPrice());
            dm.setQuantity(50); // Default daily stock
            return dm;
        }).collect(Collectors.toList());

        dailyMenuRepository.saveAll(newMenus);
        return ResponseEntity.ok(Map.of("message", "Menu Initialized with Defaults"));
    }

    @PostMapping("/daily-menu")
    public ResponseEntity<?>
    saveMenu(@RequestBody Map<String, Object> payload) {
        try {
            String dateStr = (String) payload.get("date");
            LocalDate date = LocalDate.parse(dateStr);
            List<Map<String, Object>> items = (List<Map<String, Object>>) payload.get("items");

            for (Map<String, Object> itemData : items) {
                Long itemId = Long.valueOf(itemData.get("id").toString());
                Double price = Double.valueOf(itemData.get("price").toString());
                Integer quantity = Integer.valueOf(itemData.get("available").toString());

                Optional<DailyMenu> existing = dailyMenuRepository.findByDateAndFoodItemId(date, itemId);
                DailyMenu dm = existing.orElse(new DailyMenu());

                if (existing.isEmpty()) {
                    dm.setDate(date);
                    dm.setFoodItem(foodItemRepository.findById(itemId).orElseThrow());
                }

                dm.setPrice(price);
                dm.setQuantity(quantity);
                dailyMenuRepository.save(dm);
            }

            return ResponseEntity.ok(Map.of("message", "Menu Published Successfully for " + date));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/daily-menu/{id}")
    public ResponseEntity<?> deleteDailyMenuItem(@PathVariable Long id) {
        dailyMenuRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Item Removed from Daily Menu"));
    }

    @PostMapping("/order")
    public ResponseEntity<?> placeOrder(@RequestBody Map<String, Object> payload) {
        try {
            String studentId = (String) payload.get("studentId");
            String items = (String) payload.get("items");
            Double total = Double.valueOf(payload.get("total").toString());

            // Validate Date presence (although logic doesn't strictly depend on it for
            // basic order creation,
            // the prompt implies data reflection. Ideally we should deduct stock here too).

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

            // TODO: In a real app, we would decrement stock in DailyMenu here.
            // keeping it simple for now as requested, just persisting the order.

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
        String status = order.getStatus();

        if ("CANCELLED".equalsIgnoreCase(status)) {
            return ResponseEntity.ok(Map.of(
                    "status", "CANCELLED",
                    "message", "Order Cancelled by Student", // Exact requested message behavior (corrected spelling)
                    "studentId", order.getStudentId(),
                    "items", order.getItems()));
        }

        if ("COMPLETED".equalsIgnoreCase(status)) {
            return ResponseEntity.ok(Map.of(
                    "status", "ALREADY_COMPLETED",
                    "message", "Order #" + orderId + " was already served.",
                    "studentId", order.getStudentId(),
                    "items", order.getItems()));
        }

        // If not Cancelled or Completed, assume Pending/Valid
        order.setStatus("COMPLETED");
        foodOrderRepository.save(order);

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Order #" + orderId + " is Valid. Marked as Served!",
                "studentId", order.getStudentId(),
                "items", order.getItems()));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> cancelOrder(@PathVariable String orderId) {
        Optional<FoodOrder> orderOpt = foodOrderRepository.findByOrderId(orderId);

        if (orderOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Order not found"));
        }

        FoodOrder order = orderOpt.get();
        if (!"PENDING".equalsIgnoreCase(order.getStatus())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cannot cancel processed order"));
        }

        // Hard delete or Soft delete? Usually hard delete for cancellation if not
        // processed.
        // Or update status to CANCELLED. Let's update status to keep history.
        order.setStatus("CANCELLED");
        foodOrderRepository.save(order);

        return ResponseEntity.ok(Map.of("message", "Order Cancelled Successfully"));
    }
}
