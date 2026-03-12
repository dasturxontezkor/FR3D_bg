package com.example.demo.dto;

import com.example.demo.dto.UserAnswer;

import java.util.List;

public class SubmitRequest {
    public List<UserAnswer> answers;
    public int timeSpent;    // soniyada (frontend dan keladi)
}