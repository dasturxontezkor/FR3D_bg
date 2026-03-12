package com.example.demo.controller;

import com.example.demo.dto.ResultDTO;
import com.example.demo.dto.SubmitRequest;
import com.example.demo.dto.TestFullDTO;

import com.example.demo.services.TestService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tests")
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    // GET /api/tests/{testId} → test + savollar (to'g'ri javobsiz)
    @GetMapping("/{testId}")
    public TestFullDTO getTest(@PathVariable Long testId) {
        return testService.getFullTest(testId);
    }

    // POST /api/tests/{testId}/submit → javoblarni topshirish
    // Body: { "answers": [{"questionId":1,"answerId":3}, ...], "timeSpent": 245 }
    @PostMapping("/{testId}/submit")
    public ResultDTO submit(@PathVariable Long testId,
                            @RequestBody SubmitRequest request,
                            HttpServletRequest httpRequest) {
        // JwtFilter userId ni request attribute ga qo'ygan
        Long userId = (Long) httpRequest.getAttribute("userId");
        return testService.submit(testId, request, userId);
    }
}