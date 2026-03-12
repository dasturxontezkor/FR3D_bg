package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.rep.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepo;

    public UserController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // GET /api/user/me → joriy user ma'lumotlari
    @GetMapping("/me")
    public ResponseEntity<?> getMe(HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Token kerak"));

        return userRepo.findById(userId).map(u ->
                ResponseEntity.ok(Map.of(
                        "success",  true,
                        "id",       u.getId(),
                        "username", u.getUsername(),
                        "phone",    u.getPhone() != null ? u.getPhone() : ""
                ))
        ).orElse(ResponseEntity.status(404).body(Map.of("error", "Topilmadi")));
    }

    // PUT /api/user/username → username o'zgartirish
    // Body: { "username": "yangi_nom", "password": "parol" }
    @PutMapping("/username")
    public ResponseEntity<?> updateUsername(@RequestBody Map<String, String> body,
                                            HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Token kerak"));

        String newUsername = body.get("username");
        String password    = body.get("password");

        if (newUsername == null || newUsername.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "Username bo'sh bo'lmasin"));
        if (newUsername.length() < 3)
            return ResponseEntity.badRequest().body(Map.of("error", "Username kamida 3 ta belgi bo'lishi kerak"));
        if (password == null || password.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "Parolni tasdiqlash uchun kiriting"));

        return userRepo.findById(userId).map(u -> {
            // Parol tekshiruvi
            if (!u.getPassword().equals(password))
                return ResponseEntity.status(403)
                        .<Map<String,Object>>body(Map.of("error", "Parol noto'g'ri"));

            // Username band tekshiruvi
            if (userRepo.findByUsername(newUsername).isPresent()
                    && !u.getUsername().equals(newUsername))
                return ResponseEntity.status(409)
                        .<Map<String,Object>>body(Map.of("error",
                                "\"" + newUsername + "\" username allaqachon band"));

            u.setUsername(newUsername);
            userRepo.save(u);
            return ResponseEntity.ok(Map.<String,Object>of("success", true, "username", newUsername));
        }).orElse(ResponseEntity.status(404).body(Map.of("error", "Foydalanuvchi topilmadi")));
    }

    // PUT /api/user/phone → telefon raqami o'zgartirish
    // Body: { "phone": "+998901234567", "password": "parol" }
    @PutMapping("/phone")
    public ResponseEntity<?> updatePhone(@RequestBody Map<String, String> body,
                                         HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Token kerak"));

        String newPhone = body.get("phone");
        String password = body.get("password");

        if (newPhone == null || newPhone.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "Telefon raqam bo'sh bo'lmasin"));
        if (password == null || password.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "Parolni tasdiqlash uchun kiriting"));

        return userRepo.findById(userId).map(u -> {
            if (!u.getPassword().equals(password))
                return ResponseEntity.status(403)
                        .<Map<String,Object>>body(Map.of("error", "Parol noto'g'ri"));

            u.setPhone(newPhone);
            userRepo.save(u);
            return ResponseEntity.ok(Map.<String,Object>of("success", true, "phone", newPhone));
        }).orElse(ResponseEntity.status(404).body(Map.of("error", "Foydalanuvchi topilmadi")));
    }
}