package com.example.demo.service;

import com.example.demo.entity.LeaveRequest;
import com.example.demo.repository.LeaveRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveService {

    private final LeaveRepository repo;

    public LeaveService(LeaveRepository repo) {
        this.repo = repo;
    }

    public void applyLeave(LeaveRequest request) {
        request.setStatus("PENDING");
        repo.save(request);
    }

    public List<LeaveRequest> getLeavesByStudent(String name) {
        return repo.findByStudentName(name);
    }

    public List<LeaveRequest> getAllLeaves() {
        return repo.findAll();
    }

    public void approveLeave(Long id) {
        LeaveRequest lr = repo.findById(id).orElseThrow();
        lr.setStatus("APPROVED");
        repo.save(lr);
    }

    public void rejectLeave(Long id) {
        LeaveRequest lr = repo.findById(id).orElseThrow();
        lr.setStatus("REJECTED");
        repo.save(lr);
    }
}
