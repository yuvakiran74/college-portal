package com.example.demo.repository;

import com.example.demo.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByStudentName(String studentName);

    List<LeaveRequest> findByStudentId(String studentId);

    @org.springframework.data.jpa.repository.Query("SELECT l FROM LeaveRequest l, User u WHERE LOWER(TRIM(l.studentId)) = LOWER(TRIM(u.userId)) AND LOWER(TRIM(u.section)) = LOWER(TRIM(:section))")
    List<LeaveRequest> findBySection(@org.springframework.data.repository.query.Param("section") String section);

    @org.springframework.data.jpa.repository.Query("SELECT l FROM LeaveRequest l, User u WHERE LOWER(TRIM(l.studentId)) = LOWER(TRIM(u.userId)) AND LOWER(TRIM(u.section)) = LOWER(TRIM(:section)) AND LOWER(TRIM(u.branch)) = LOWER(TRIM(:branch)) AND LOWER(TRIM(u.year)) = LOWER(TRIM(:year))")
    List<LeaveRequest> findByFacultyCriteria(@org.springframework.data.repository.query.Param("section") String section,
            @org.springframework.data.repository.query.Param("branch") String branch,
            @org.springframework.data.repository.query.Param("year") String year);
}
