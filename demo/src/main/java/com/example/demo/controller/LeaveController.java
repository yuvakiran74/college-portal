package com.example.demo.controller;

import com.example.demo.entity.LeaveRequest;
import com.example.demo.service.LeaveService;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/leave")
@CrossOrigin
public class LeaveController {

    private final LeaveService service;
    private final com.example.demo.repository.UserRepository userRepo;

    public LeaveController(LeaveService service, com.example.demo.repository.UserRepository userRepo) {
        this.service = service;
        this.userRepo = userRepo;
    }

    @PostMapping("/apply")
    public org.springframework.http.ResponseEntity<String> applyLeave(
            @RequestParam("studentName") String studentName,
            @RequestParam("studentId") String studentId,
            @RequestParam("reason") String reason,
            @RequestParam("startDate") String startDate,
            @RequestParam("leaveType") String leaveType,
            @RequestParam(value = "days", required = false) Integer days,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        try {
            String cleanId = studentId.trim();
            com.example.demo.entity.User student = userRepo.findByUserId(cleanId).orElse(null);

            if (student == null) {
                System.out.println("ERROR: Student ID not found: " + cleanId);
                return org.springframework.http.ResponseEntity.badRequest()
                        .body("Error: Student ID not found in records.");
            }

            if (student.getSection() == null || student.getSection().isEmpty()) {
                System.out.println("ERROR: Student has no section assigned: " + cleanId);
                return org.springframework.http.ResponseEntity.badRequest()
                        .body("Error: Your profile has no assigned section. Contact Admin.");
            }

            LeaveRequest request = new LeaveRequest();
            request.setStudentName(studentName);
            request.setStudentId(cleanId);
            request.setSection(student.getSection()); // Reliable assignment

            System.out.println("Applying leave for: " + cleanId + " Section: " + student.getSection());

            request.setReason(reason);
            request.setStartDate(startDate);
            request.setLeaveType(leaveType);
            request.setDays(days);

            if (file != null && !file.isEmpty()) {
                try {
                    String uploadDir = "uploads/";
                    File dir = new File(uploadDir);
                    if (!dir.exists())
                        dir.mkdirs();

                    String filePath = uploadDir + file.getOriginalFilename();
                    Files.write(Paths.get(filePath), file.getBytes());
                    request.setDocumentPath(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                    return org.springframework.http.ResponseEntity.status(500)
                            .body("Error uploading file: " + e.getMessage());
                }
            }

            service.applyLeave(request);
            return org.springframework.http.ResponseEntity.ok("Application Submitted Successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return org.springframework.http.ResponseEntity.status(500).body("Internal Error: " + e.getMessage());
        }
    }

    // Student views his leaves
    @GetMapping("/student/{name}")
    public List<LeaveRequest> studentLeaves(@PathVariable String name) {
        return service.getLeavesByStudent(name);
    }

    // Student views his leaves by ID (More Reliable)
    @GetMapping("/student-id/{id}")
    public List<LeaveRequest> studentLeavesById(@PathVariable String id) {
        return service.getLeavesByStudentId(id);
    }

    // Faculty views leaves by section
    @GetMapping("/section/{section}")
    public List<LeaveRequest> sectionLeaves(@PathVariable String section) {
        return service.getLeavesBySection(section);
    }

    // Faculty views all leaves
    @GetMapping("/all")
    public List<LeaveRequest> allLeaves() {
        return service.getAllLeaves();
    }

    // Faculty approves
    @PutMapping("/approve/{id}")
    public String approve(@PathVariable Long id) {
        service.approveLeave(id);
        return "Leave approved";
    }

    // Faculty rejects
    @PutMapping("/reject/{id}")
    public String reject(@PathVariable Long id) {
        service.rejectLeave(id);
        return "Leave rejected";
    }
}
