package com.example.demo.controller;

import com.example.demo.dto.ResultDTO;
import com.example.demo.dto.StatsDTO;
import com.example.demo.services.ResultService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/results")
public class ResultController {

    private final ResultService resultService;

    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }

    // GET /api/results/my
    @GetMapping("/my")
    public ResponseEntity<?> getMyResults(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Token kerak"));
        List<ResultDTO> results = resultService.getMyResults(userId);
        return ResponseEntity.ok(Map.of("success", true, "results", results));
    }

    // GET /api/results/test/{testId}
    @GetMapping("/test/{testId}")
    public ResponseEntity<?> getByTest(@PathVariable Long testId,
                                       HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Token kerak"));
        List<ResultDTO> results = resultService.getResultsByTest(userId, testId);
        return ResponseEntity.ok(Map.of("success", true, "results", results));
    }

    // GET /api/results/stats
    @GetMapping("/stats")
    public ResponseEntity<?> getStats(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Token kerak"));
        StatsDTO stats = resultService.getStats(userId);
        return ResponseEntity.ok(Map.of("success", true, "stats", stats));
    }
}