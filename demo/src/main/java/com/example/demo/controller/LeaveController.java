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

    public LeaveController(LeaveService service) {
        this.service = service;
    }

    // Student applies leave
    @PostMapping("/apply")
    public String applyLeave(
            @RequestParam("studentName") String studentName,
            @RequestParam("studentId") String studentId,
            @RequestParam("reason") String reason,
            @RequestParam("startDate") String startDate,
            @RequestParam("leaveType") String leaveType,
            @RequestParam(value = "days", required = false) Integer days,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        LeaveRequest request = new LeaveRequest();
        request.setStudentName(studentName);
        request.setStudentId(studentId);
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
                return "Error uploading file";
            }
        }

        service.applyLeave(request);
        return "Leave applied successfully";
    }

    // Student views his leaves
    @GetMapping("/student/{name}")
    public List<LeaveRequest> studentLeaves(@PathVariable String name) {
        return service.getLeavesByStudent(name);
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
