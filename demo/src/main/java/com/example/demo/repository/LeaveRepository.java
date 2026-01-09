package com.example.demo.repository;

import com.example.demo.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByStudentName(String studentName);

    List<LeaveRequest> findByStudentId(String studentId);

    List<LeaveRequest> findBySection(String section);
}
