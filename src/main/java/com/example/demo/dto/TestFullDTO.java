package com.example.demo.dto;

import java.util.List;

// GET /api/tests/{id} → TestFullDTO
public class TestFullDTO {
    public Long id;
    public String name;
    public int time;                     // daqiqada
    public List<QuestionDTO> questions;
}