CREATE DATABASE IF NOT EXISTS leave_outpass_db;
USE leave_outpass_db;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) UNIQUE NOT NULL, -- The new User ID for login
    name VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255),
    role VARCHAR(50) -- 'STUDENT' or 'FACULTY'
);

-- Leave Requests Table
CREATE TABLE IF NOT EXISTS leave_request (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_name VARCHAR(255),
    leave_type VARCHAR(50), -- 'LEAVE' or 'OUTPASS'
    start_date VARCHAR(50),
    days INT,
    reason TEXT,
    status VARCHAR(50) DEFAULT 'PENDING',
    document_path VARCHAR(500)
);

-- Optional: Insert a test student to verify login immediately
-- Password is 'password' (you might need to use plain text if you aren't hashing, or hashed if you are)
-- INSERT INTO users (user_id, name, email, password, role) VALUES ('teststudent', 'Test Student', 'test@example.com', 'password', 'STUDENT');
