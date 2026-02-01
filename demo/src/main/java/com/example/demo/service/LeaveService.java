package com.example.demo.service;

import com.example.demo.entity.LeaveRequest;
import com.example.demo.repository.LeaveRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveService {

    private final LeaveRepository repo;
    private final com.example.demo.repository.UserRepository userRepo;

    public LeaveService(LeaveRepository repo, com.example.demo.repository.UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    public void applyLeave(LeaveRequest request) {
        String studentId = request.getStudentId();
        com.example.demo.entity.User user = userRepo.findByUserId(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Check Limits
        checkLimit(user, request.getLeaveType());

        // CRITICAL FIX: Save the student's section!
        request.setSection(user.getSection());
        request.setStatus("PENDING");
        repo.save(request);
    }

    // Repair method to fix existing data
    @jakarta.annotation.PostConstruct
    public void repairMissingSections() {
        List<LeaveRequest> all = repo.findAll();
        boolean changed = false;
        for (LeaveRequest lr : all) {
            if (lr.getSection() == null || lr.getSection().isEmpty()) {
                userRepo.findByUserId(lr.getStudentId()).ifPresent(u -> {
                    lr.setSection(u.getSection());
                    repo.save(lr);
                });
                changed = true;
            }
        }
        if (changed)
            System.out.println("Repaired missing sections in LeaveRequests.");
    }

    private void checkLimit(com.example.demo.entity.User user, String type) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        // If admin reset the limit recently (this month), use that date instead
        if (user.getLastLimitResetDate() != null && user.getLastLimitResetDate().isAfter(startOfMonth)) {
            startOfMonth = user.getLastLimitResetDate();
        }

        List<LeaveRequest> history = repo.findByStudentId(user.getUserId());

        // Effectively final for lambda
        java.time.LocalDateTime checkFrom = startOfMonth;

        System.out.println("Checking limits for " + user.getUserId() + " Type: " + type + " From: " + checkFrom);

        long count = history.stream()
                .filter(l -> l.getLeaveType().equalsIgnoreCase(type)) // LEAVE or OUTPASS
                .filter(l -> !l.getStatus().equals("REJECTED")) // Pending or Approved count towards limit
                .filter(l -> {
                    // Parse Start Date to check if it falls in current window
                    try {
                        if (l.getStartDate() == null)
                            return false;
                        // Assuming format yyyy-MM-dd from input
                        java.time.LocalDate date = java.time.LocalDate.parse(l.getStartDate());
                        boolean inRange = date.atStartOfDay().isAfter(checkFrom)
                                || date.atStartOfDay().isEqual(checkFrom);
                        System.out.println(" - Leave Date: " + l.getStartDate() + " InRange: " + inRange);
                        return inRange;
                    } catch (Exception e) {
                        System.out.println(" - Date Parse Error: " + l.getStartDate());
                        return false; // Skip malformed dates
                    }
                })
                .count();

        System.out.println("Total Count for " + type + ": " + count);

        if (count >= 3) {
            throw new RuntimeException(
                    "your limit reached for booking this month and contact your admin for rrseting for this month");
        }
    }

    public void computeLimitStatus(com.example.demo.entity.User user) {
        try {
            // Check if BOTH are okay or if ANY is reached?
            // The requirement says "limit reached... contact admin", implying if blocked.
            // We'll check if EITHER is blocked to show the Reset button.
            checkLimit(user, "LEAVE");
            checkLimit(user, "OUTPASS");
            user.setLimitReached(false);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("limit reached")) {
                user.setLimitReached(true);
            }
        }
    }

    public List<LeaveRequest> getLeavesByStudent(String name) {
        return repo.findByStudentName(name);
    }

    public List<LeaveRequest> getLeavesByStudentId(String studentId) {
        return repo.findByStudentId(studentId);
    }

    public List<LeaveRequest> getLeavesBySection(String section) {
        return repo.findBySection(section);
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
