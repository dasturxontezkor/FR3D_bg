package com.example.demo.controller;

import com.example.demo.dto.LoginResponse;
import com.example.demo.model.User;
import com.example.demo.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // POST /api/auth/register
    // Body: { "username": "ali", "password": "123456", "phone": "+998901234567" }
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody User user) {
        LoginResponse res = userService.register(user);
        int status = res.success ? 201 : 409;
        return ResponseEntity.status(status).body(res);
    }

    // POST /api/auth/login
    // Body: { "username": "ali", "password": "123456" }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody User user) {
        LoginResponse res = userService.login(user.getUsername(), user.getPassword());
        int status = res.success ? 200 : 401;
        return ResponseEntity.status(status).body(res);
    }
}