package com.example.demo.dto;

// GET /api/results/stats → StatsDTO (dashboard uchun)
public class StatsDTO {
    public long totalTests;
    public double avgScore;
    public double bestScore;
    public long totalTimeSpent;   // soniyada
}