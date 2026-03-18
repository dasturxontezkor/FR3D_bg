package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.rep.*;
import com.example.demo.services.TestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {


    private final LevelRepository    levelRepo;
    private final TestRepository     testRepo;
    private final QuestionRepository questionRepo;
    private final AnswerRepository   answerRepo;
    private final UserRepository     userRepo;
    private final ResultRepository   resultRepo;
    private final TestService        testService;
private final JwtUtil jwtUtil;
    public AdminController(LevelRepository lr, TestRepository tr,
                           QuestionRepository qr, AnswerRepository ar,
                           UserRepository ur, ResultRepository rr,
                           TestService ts) {
        levelRepo = lr; testRepo = tr; questionRepo = qr;
        answerRepo = ar; userRepo = ur; resultRepo = rr; testService = ts;
    }
    public AdminController(LevelRepository lr, TestRepository tr,
                       QuestionRepository qr, AnswerRepository ar,
                       UserRepository ur, ResultRepository rr,
                       TestService ts, JwtUtil ju) {
    levelRepo=lr; testRepo=tr; questionRepo=qr;
    answerRepo=ar; userRepo=ur; resultRepo=rr;
    testService=ts; jwtUtil=ju;
}


   

    // ── STATS ─────────────────────────────────────────────────────
    @GetMapping("/stats")
    public ResponseEntity<?> stats(HttpServletRequest req) {
        if (!isAdmin(req)) return forbidden();
        return ResponseEntity.ok(Map.of(
                "levels",    levelRepo.count(),
                "tests",     testRepo.count(),
                "questions", questionRepo.count(),
                "users",     userRepo.count()
        ));
    }

    // ── LEVELS ────────────────────────────────────────────────────
    @GetMapping("/levels")
    public ResponseEntity<?> getLevels(HttpServletRequest req) {
        if (!isAdmin(req)) return forbidden();
        List<Map<String,Object>> list = new ArrayList<>();
        for (Level l : levelRepo.findAll()) {
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("id",          l.getId());
            m.put("name",        l.getName());
            m.put("description", l.getDescription());
            m.put("testCount",   testRepo.findByLevelId(l.getId()).size());
            list.add(m);
        }
        return ResponseEntity.ok(Map.of("levels", list));
    }

    @PostMapping("/levels")
    public ResponseEntity<?> addLevel(@RequestBody Map<String,String> body, HttpServletRequest req) {
        if (!isAdmin(req)) return forbidden();
        Level l = new Level();
        l.setName(body.get("name"));
        l.setDescription(body.get("description"));
        levelRepo.save(l);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PutMapping("/levels/{id}")
    public ResponseEntity<?> updateLevel(@PathVariable Long id,
                                         @RequestBody Map<String,String> body,
                                         HttpServletRequest req) {
        if (!isAdmin(req)) return forbidden();
        return levelRepo.findById(id).map(l -> {
            l.setName(body.get("name"));
            l.setDescription(body.get("description"));
            levelRepo.save(l);
            return ResponseEntity.ok(Map.of("success", true));
        }).orElse(ResponseEntity.notFound().build());
    }

@DeleteMapping("/levels/{id}")
public ResponseEntity<?> deleteLevel(@PathVariable Long id) {
    for (Test t : testRepo.findByLevelId(id)) {
        deleteTestCascade(t.getId());
    }
    levelRepo.deleteById(id);
    return ResponseEntity.ok(Map.of("success", true));
}

    // ── TESTS ─────────────────────────────────────────────────────
    @GetMapping("/levels/{lid}/tests")
    public ResponseEntity<?> getTests(@PathVariable Long lid, HttpServletRequest req) {
        if (!isAdmin(req)) return forbidden();
        List<Map<String,Object>> list = new ArrayList<>();
        for (Test t : testRepo.findByLevelId(lid)) {
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("id",            t.getId());
            m.put("name",          t.getName());
            m.put("timeMinutes",   t.getTimeMinutes());
            m.put("questionCount", questionRepo.findByTestId(t.getId()).size());
            list.add(m);
        }
        return ResponseEntity.ok(Map.of("tests", list));
    }

    @PostMapping("/levels/{lid}/tests")
    public ResponseEntity<?> addTest(@PathVariable Long lid,
                                     @RequestBody Map<String,Object> body,
                                     HttpServletRequest req) {
        if (!isAdmin(req)) return forbidden();
        return levelRepo.findById(lid).map(lv -> {
            Test t = new Test();
            t.setName((String) body.get("name"));
            t.setTimeMinutes(((Number) body.get("timeMinutes")).intValue());
            t.setLevel(lv);
            testRepo.save(t);
            return ResponseEntity.ok(Map.of("success", true));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/tests/{id}")
    public ResponseEntity<?> updateTest(@PathVariable Long id,
                                        @RequestBody Map<String,Object> body,
                                        HttpServletRequest req) {
        if (!isAdmin(req)) return forbidden();
        return testRepo.findById(id).map(t -> {
            t.setName((String) body.get("name"));
            t.setTimeMinutes(((Number) body.get("timeMinutes")).intValue());
            testRepo.save(t);
            return ResponseEntity.ok(Map.of("success", true));
        }).orElse(ResponseEntity.notFound().build());
    }

@DeleteMapping("/tests/{id}")
public ResponseEntity<?> deleteTest(@PathVariable Long id) {
    deleteTestCascade(id);
    return ResponseEntity.ok(Map.of("success", true));
}
    // ── QUESTIONS ─────────────────────────────────────────────────
    @GetMapping("/tests/{tid}/questions")
    public ResponseEntity<?> getQuestions(@PathVariable Long tid, HttpServletRequest req) {
        if (!isAdmin(req)) return forbidden();
        List<Map<String,Object>> list = new ArrayList<>();
        for (Question q : questionRepo.findByTestId(tid)) {
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("id",           q.getId());
            m.put("questionText", q.getQuestionText());
            List<Map<String,Object>> ansList = new ArrayList<>();
            for (Answer a : answerRepo.findByQuestionId(q.getId())) {
                Map<String,Object> am = new LinkedHashMap<>();
                am.put("id",         a.getId());
                am.put("answerText", a.getAnswerText());
                am.put("correct",    a.isCorrect());
                ansList.add(am);
            }
            m.put("answers", ansList);
            list.add(m);
        }
        return ResponseEntity.ok(Map.of("questions", list));
    }

@DeleteMapping("/questions/{id}")
public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
    answerRepo.deleteByQuestionId(id);
    questionRepo.deleteById(id);
    return ResponseEntity.ok(Map.of("success", true));
}

    // ── WORD UPLOAD ───────────────────────────────────────────────
    @PostMapping("/tests/{tid}/upload-word")
    public ResponseEntity<?> uploadWord(@PathVariable Long tid,
                                        @RequestParam("file") MultipartFile file,
                                        HttpServletRequest req) {
        if (!isAdmin(req)) return forbidden();
        try {
            int count = testService.importFromWord(tid, file);
            return ResponseEntity.ok(Map.of("success", true, "questionsImported", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    // ── RESULTS ───────────────────────────────────────────────────
    // UserResult.userId bor, lekin User entity yo'q → userRepo dan username olamiz
    @GetMapping("/tests/{tid}/results")
    public ResponseEntity<?> getTestResults(@PathVariable Long tid, HttpServletRequest req) {
        if (!isAdmin(req)) return forbidden();

        List<UserResult> results = resultRepo.findByTestId(tid);
        List<Map<String,Object>> list = new ArrayList<>();

        for (UserResult r : results) {
            // userId orqali username ni topamiz (null-safe)
            String username = "—";
            if (r.getUserId() != null) {
                username = userRepo.findById(r.getUserId())
                        .map(User::getUsername)
                        .orElse("—");
            }

            Map<String,Object> m = new LinkedHashMap<>();
            m.put("username",   username);
            m.put("score",      r.getScore());
            m.put("total",      r.getTotal());
            m.put("percentage", r.getPercentage());
            m.put("timeSpent",  r.getTimeSpent());
            m.put("finishedAt", r.getFinishedAt());
            list.add(m);
        }

        // Foiz bo'yicha kamayish tartibida
        list.sort((a, b) -> Double.compare(
                ((Number) b.get("percentage")).doubleValue(),
                ((Number) a.get("percentage")).doubleValue()
        ));

        return ResponseEntity.ok(Map.of("results", list));
    }

    // ── USERS ─────────────────────────────────────────────────────
    @GetMapping("/users")
    public ResponseEntity<?> getUsers(HttpServletRequest req) {
        if (!isAdmin(req)) return forbidden();
        List<Map<String,Object>> list = new ArrayList<>();
        for (User u : userRepo.findAll()) {
            if (ADMIN_USERNAME.equals(u.getUsername())) continue;
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("id",       u.getId());
            m.put("username", u.getUsername());
            m.put("phone",    u.getPhone());
            list.add(m);
        }
        return ResponseEntity.ok(Map.of("users", list));
    }
}

private void deleteTestCascade(Long testId) {
    for (Question q : questionRepo.findByTestId(testId)) {
        answerRepo.deleteByQuestionId(q.getId());
    }
    questionRepo.deleteByTestId(testId);
    resultRepo.deleteByTestId(testId);
    testRepo.deleteById(testId);
}
