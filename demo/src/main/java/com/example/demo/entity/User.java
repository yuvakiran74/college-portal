package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;
    private String name;
    private String email;
    private String password;
    private String role; // STUDENT or FACULTY
    private String section;

    private java.time.LocalDateTime lastLimitResetDate;

    @Transient
    private boolean isLimitReached;

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public java.time.LocalDateTime getLastLimitResetDate() {
        return lastLimitResetDate;
    }

    public void setLastLimitResetDate(java.time.LocalDateTime lastLimitResetDate) {
        this.lastLimitResetDate = lastLimitResetDate;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("limitReached")
    public boolean isLimitReached() {
        return isLimitReached;
    }

    public void setLimitReached(boolean limitReached) {
        isLimitReached = limitReached;
    }
}
