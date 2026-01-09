package com.example.demo.repository;

import com.example.demo.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByUserId(String userId);

    Optional<Attendance> findByUserIdAndDate(String userId, LocalDate date);

    List<Attendance> findAllByOrderByDateDesc();

    List<Attendance> findByUserIdInAndDate(List<String> userIds, LocalDate date);

    List<Attendance> findByUserIdInAndDateBetween(List<String> userIds, LocalDate startDate, LocalDate endDate);
}
