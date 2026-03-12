package com.example.demo.dto;

// GET /api/levels/{id}/tests → TestDTO
public class TestDTO {
    public Long id;
    public String name;
    public int questions;
    public String time;       // "10 min"
    public String status;     // "new" | "done" | "locked"
}