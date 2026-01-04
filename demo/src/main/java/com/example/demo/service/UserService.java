package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // REGISTER USER
    public User register(User user) {
        return userRepository.save(user);
    }

    // LOGIN USER
    public Optional<User> login(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }
}
