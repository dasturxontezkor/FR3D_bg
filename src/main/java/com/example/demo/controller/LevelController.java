package com.example.demo.controller;

import com.example.demo.JwtUtil;
import com.example.demo.dto.LevelDTO;
import com.example.demo.dto.TestDTO;
import com.example.demo.services.LevelService;
import com.example.demo.services.TestService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/levels")
public class LevelController {

    private final LevelService levelService;
    private final TestService  testService;
    private final JwtUtil jwtUtil;

    public LevelController(LevelService levelService,
                           TestService testService,
                           JwtUtil jwtUtil) {
        this.levelService = levelService;
        this.testService  = testService;
        this.jwtUtil      = jwtUtil;
    }

    // GET /api/levels → barcha levellar
    @GetMapping
    public Map<String, Object> getAllLevels() {
        List<LevelDTO> levels = levelService.getAllLevels();
        return Map.of("levels", levels);
    }

    // GET /api/levels/{levelId}/tests → level testlari (status bilan)
    @GetMapping("/{levelId}/tests")
    public Map<String, Object> getTests(@PathVariable Long levelId,
                                        HttpServletRequest request) {
        // JwtFilter userId ni request attribute ga qo'ygan
        Long userId = (Long) request.getAttribute("userId");
        List<TestDTO> tests = testService.getTestsByLevel(levelId, userId);
        return Map.of("tests", tests);
    }
}