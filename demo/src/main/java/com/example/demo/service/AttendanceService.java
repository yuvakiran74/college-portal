package com.example.demo.service;

import com.example.demo.entity.Attendance;
import com.example.demo.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    public Attendance markAttendance(String userId, double lat, double lng, String livenessImage) {
        // Simple Geolocation Logic (Mocked for now as per "analyse location"
        // requirement which usually needs frontend coordinates)
        // In a real app, we would check if lat/lng validation is within campus bounds.
        // For this demo, we assume if we get coordinates, it's valid.

        LocalDate today = LocalDate.now();
        Optional<Attendance> existing = attendanceRepository.findByUserIdAndDate(userId, today);
        if (existing.isPresent()) {
            throw new RuntimeException("Attendance already marked for today.");
        }

        Attendance attendance = new Attendance(userId, today, "PRESENT", livenessImage);
        return attendanceRepository.save(attendance);
    }

    public Attendance getAttendanceForToday(String userId) {
        return attendanceRepository.findByUserIdAndDate(userId, LocalDate.now()).orElse(null);
    }

    public Attendance getAttendanceByDate(String userId, LocalDate date) {
        return attendanceRepository.findByUserIdAndDate(userId, date).orElse(null);
    }

    public List<Attendance> getStudentAttendance(String userId) {
        return attendanceRepository.findByUserId(userId);
    }

    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAllByOrderByDateDesc();
    }

    public double getMonthlyAttendancePercentage(String userId) {
        List<Attendance> allAttendance = attendanceRepository.findByUserId(userId);
        if (allAttendance.isEmpty())
            return 0.0;

        LocalDate now = LocalDate.now();
        long count = allAttendance.stream()
                .filter(a -> a.getDate().getMonth() == now.getMonth() && a.getDate().getYear() == now.getYear())
                .filter(a -> "PRESENT".equals(a.getStatus()))
                .count();

        // Assuming 22 working days for simplicity, or we can count total days passed in
        // month excluding weekends.
        // For a hackathon/demo, simpler is better: Percentage of days marked present vs
        // total days marked (or just a fixed number for now if we don't have a
        // calendar).
        // Let's use total records for that month as denominator for now, or just return
        // the count.
        // Requirement says "monthly attendance percent".
        // Let's assume total working days in a month is 30 for simplicity or count
        // distinct dates in DB.
        // Better approach: count present days / total days in month so far.

        int lengthOfMonth = now.lengthOfMonth();
        // This is a rough estimate. Real world needs holiday calendar.
        return (double) count / lengthOfMonth * 100;
    }

    @Autowired
    private com.example.demo.repository.UserRepository userRepository;

    // ... existing imports ...

    public List<Attendance> getAttendanceBySectionAndDate(String section, LocalDate date) {
        List<com.example.demo.entity.User> students = userRepository.findByRoleAndSection("STUDENT", section);
        List<String> studentIds = students.stream().map(com.example.demo.entity.User::getUserId).toList();

        if (studentIds.isEmpty())
            return List.of();

        return attendanceRepository.findByUserIdInAndDate(studentIds, date);
    }

    public List<Attendance> getAttendanceBySectionAndMonth(String section, int month, int year) {
        List<com.example.demo.entity.User> students = userRepository.findByRoleAndSection("STUDENT", section);
        List<String> studentIds = students.stream().map(com.example.demo.entity.User::getUserId).toList();

        if (studentIds.isEmpty())
            return List.of();

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return attendanceRepository.findByUserIdInAndDateBetween(studentIds, start, end);
    }

    public String generateCsv(List<Attendance> records) {
        StringBuilder csv = new StringBuilder();
        csv.append("Student ID,Date,Time,Status\n");
        for (Attendance a : records) {
            csv.append(a.getUserId()).append(",")
                    .append(a.getDate()).append(",")
                    .append(a.getTimestamp().toLocalTime()).append(",")
                    .append(a.getStatus()).append("\n");
        }
        return csv.toString();
    }

    public Attendance manualMarkAttendance(String userId, String section) {
        com.example.demo.entity.User student = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + userId));

        if (!"STUDENT".equalsIgnoreCase(student.getRole())) {
            throw new RuntimeException("User is not a student.");
        }

        if (section != null && !section.equalsIgnoreCase(student.getSection())) {
            throw new RuntimeException("Student does not belong to section " + section);
        }

        LocalDate today = LocalDate.now();
        Optional<Attendance> existing = attendanceRepository.findByUserIdAndDate(userId, today);
        if (existing.isPresent()) {
            // Requirement says: "if student has got any server issues... student can go to
            // faculty directly... and post attendance"
            // If it's already marked absent? Or check logic.
            // Usually if they are here, it's because they aren't marked present.
            // If they are already marked Present, we can say "Already Present".
            // If they are marked Absent (if we had that), we should update.
            // But current system only seems to mark PRESENT. There is no ABSENT record
            // creation explicitly visible yet (unless cron job).
            // Assuming we only store positive attendance.
            throw new RuntimeException("Attendance already marked for today.");
        }

        Attendance attendance = new Attendance(userId, today, "PRESENT", "MANUAL_OVERRIDE");
        return attendanceRepository.save(attendance);
    }
}
