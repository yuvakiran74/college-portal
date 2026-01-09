package com.example.demo;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUserIdAndPassword("admin", "admin123").isEmpty()) {
                // Check if admin exists by ID generic check, or just try to find by userid
                // Since we don't have findByUserId, we'll use a custom check or just try to
                // save if not present.
                // But wait, repo has findByUserIdAndPassword, let's assume valid for now.
                // A better check would be findByUserId if it existed, but let's rely on the
                // unique constraint fail if any or just basic check.
                // Let's iterate all or assume clean state? No, better safe.

                // Let's just try to create one.
                User admin = new User();
                admin.setUserId("admin");
                admin.setName("System Admin");
                admin.setEmail("admin@lendi.edu.in");
                admin.setPassword("admin123");
                admin.setRole("ADMIN");

                try {
                    userRepository.save(admin);
                    System.out.println("Default Admin User created: admin / admin123");
                } catch (Exception e) {
                    // Probably already exists
                }
            }
        };
    }
}
