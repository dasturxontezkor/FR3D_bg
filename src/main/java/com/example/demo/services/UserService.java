package com.example.demo.services;

import com.example.demo.JwtUtil;
import com.example.demo.dto.LoginResponse;
import com.example.demo.model.User;
import com.example.demo.rep.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil        = jwtUtil;
    }

    // ── Register ─────────────────────────────────────────
    // Sizning asl kodingiz bilan bir xil, faqat LoginResponse qaytaradi
    public LoginResponse register(User user) {
        System.out.println("Register request: " + user);

        Optional<User> existing = userRepository.findByUsername(user.getUsername());
        if (existing.isPresent()) {
            return new LoginResponse(false, "Username already taken", null, null, null);
        }

        // ⚠️ Hozir plain-text parol saqlanadi (sizning asl kodingizday)
        // Keyinchalik: BCryptPasswordEncoder qo'shing
        userRepository.save(user);
        System.out.println("User saved: " + user.getUsername());

        // Ro'yxatdan o'tgandan keyin darhol token beradi
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        return new LoginResponse(true, "Registered successfully", token, user.getId(), user.getUsername());
    }

    // ── Login ─────────────────────────────────────────────
    // Sizning asl login logikangiz + JWT token
    public LoginResponse login(String username, String password) {
        System.out.println("Login attempt: " + username);

        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            User   user  = userOpt.get();
            String token = jwtUtil.generateToken(user.getId(), user.getUsername());
            System.out.println("Login successful: " + username);
            return new LoginResponse(true, "Login successful", token, user.getId(), user.getUsername());
        }

        System.out.println("Login failed: " + username);
        return new LoginResponse(false, "Invalid credentials", null, null, null);
    }
}