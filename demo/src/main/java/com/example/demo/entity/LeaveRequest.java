package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentName;
    private String studentId; // New field for User ID
    private String reason;
    private String status = "PENDING"; // Default to PENDING

    private String startDate;
    private String leaveType; // LEAVE / OUTPASS
    private Integer days;
    private String documentPath;

    public Long getId() {
        return id;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public Integer getDays() {
        return days;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }
}
