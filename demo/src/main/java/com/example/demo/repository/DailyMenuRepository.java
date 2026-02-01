package com.example.demo.repository;

import com.example.demo.entity.DailyMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyMenuRepository extends JpaRepository<DailyMenu, Long> {
    List<DailyMenu> findByDate(LocalDate date);

    Optional<DailyMenu> findByDateAndFoodItemId(LocalDate date, Long foodItemId);
}
