package com.example.demo.controller;

import com.example.demo.entity.Attendance;
import com.example.demo.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/mark")
    public ResponseEntity<?> markAttendance(@RequestBody Map<String, Object> payload) {
        String userId = (String) payload.get("userId");
        Double lat = Double.valueOf(payload.get("latitude").toString());
        Double lng = Double.valueOf(payload.get("longitude").toString());
        String livenessImage = (String) payload.get("livenessImage");

        try {
            Attendance attendance = attendanceService.markAttendance(userId, lat, lng, livenessImage);
            return ResponseEntity.ok(attendance);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/student/{userId}")
    public ResponseEntity<List<Attendance>> getStudentAttendance(@PathVariable String userId) {
        return ResponseEntity.ok(attendanceService.getStudentAttendance(userId));
    }

    @GetMapping("/student/{userId}/today")
    public ResponseEntity<Attendance> getAttendanceForToday(@PathVariable String userId) {
        Attendance attendance = attendanceService.getAttendanceForToday(userId);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/student/{userId}/date/{date}")
    public ResponseEntity<Attendance> getAttendanceByDate(@PathVariable String userId, @PathVariable String date) {
        java.time.LocalDate localDate = java.time.LocalDate.parse(date);
        Attendance attendance = attendanceService.getAttendanceByDate(userId, localDate);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/student/{userId}/monthly")
    public ResponseEntity<Double> getMonthlyPercentage(@PathVariable String userId) {
        return ResponseEntity.ok(attendanceService.getMonthlyAttendancePercentage(userId));
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateLocation(@RequestBody Map<String, Double> coordinates) {
        Double lat = coordinates.get("latitude");
        Double lng = coordinates.get("longitude");

        // Target: 18.020429, 83.400911
        double targetLat = 18.020429;
        double targetLng = 83.400911;
        double allowedRadiusKm = 0.2; // 200 meters

        double distance = calculateDistance(lat, lng, targetLat, targetLng);

        if (distance <= allowedRadiusKm) {
            return ResponseEntity.ok(Map.of("valid", true, "message", "Location Verified: On Campus"));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "valid", false,
                    "message", String.format("You are not on campus. Detected %.2fkm away.", distance)));
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Attendance>> getAllAttendance() {
        return ResponseEntity.ok(attendanceService.getAllAttendance());
    }

    @GetMapping("/section")
    public ResponseEntity<List<Attendance>> getBySection(
            @RequestParam String section,
            @RequestParam(required = false) String date) {

        java.time.LocalDate d = (date == null || date.isEmpty()) ? java.time.LocalDate.now()
                : java.time.LocalDate.parse(date);
        return ResponseEntity.ok(attendanceService.getAttendanceBySectionAndDate(section, d));
    }

    @GetMapping("/download")
    public ResponseEntity<String> downloadAttendance(
            @RequestParam String section,
            @RequestParam String type, // DAILY or MONTHLY
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {

        List<Attendance> data;
        String filename;

        if ("MONTHLY".equalsIgnoreCase(type)) {
            int m = (month == null) ? java.time.LocalDate.now().getMonthValue() : month;
            int y = (year == null) ? java.time.LocalDate.now().getYear() : year;
            data = attendanceService.getAttendanceBySectionAndMonth(section, m, y);
            filename = "Attendance_" + section + "_" + m + "_" + y + ".csv";
        } else {
            java.time.LocalDate d = (date == null || date.isEmpty()) ? java.time.LocalDate.now()
                    : java.time.LocalDate.parse(date);
            data = attendanceService.getAttendanceBySectionAndDate(section, d);
            filename = "Attendance_" + section + "_" + d + ".csv";
        }

        String csvContent = attendanceService.generateCsv(data);

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "text/csv")
                .body(csvContent);
    }

    @PostMapping("/manual")
    public ResponseEntity<?> manualAttendance(@RequestBody Map<String, String> payload) {
        String userId = payload.get("userId");
        String section = payload.get("section");
        try {
            Attendance a = attendanceService.manualMarkAttendance(userId, section);
            return ResponseEntity.ok(a);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
