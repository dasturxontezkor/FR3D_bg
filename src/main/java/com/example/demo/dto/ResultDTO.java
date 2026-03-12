package com.example.demo.dto;

import java.time.LocalDateTime;

// POST /api/tests/{id}/submit → ResultDTO
public class ResultDTO {
    public Long id;
    public int score;
    public int total;
    public double percentage;
    public int timeSpent;
    public String testName;
    public LocalDateTime finishedAt;
}