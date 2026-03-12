package com.example.demo.services;

import com.example.demo.dto.*;
import com.example.demo.model.*;
import com.example.demo.rep.*;

import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class TestService {

    private final TestRepository     testRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository   answerRepository;
    private final ResultRepository   resultRepository;

    public TestService(TestRepository testRepository,
                       QuestionRepository questionRepository,
                       AnswerRepository answerRepository,
                       ResultRepository resultRepository) {
        this.testRepository     = testRepository;
        this.questionRepository = questionRepository;
        this.answerRepository   = answerRepository;
        this.resultRepository   = resultRepository;
    }

    // ── GET /api/levels/{levelId}/tests ──────────────────
    public List<TestDTO> getTestsByLevel(Long levelId, Long userId) {
        List<Test> tests = testRepository.findByLevelId(levelId);
        return tests.stream().map(t -> {
            TestDTO dto  = new TestDTO();
            dto.id        = t.getId();
            dto.name      = t.getName();
            dto.questions = t.getQuestionCount();
            dto.time      = t.getTimeMinutes() + " min";
            dto.status    = resolveStatus(t.getId(), userId);
            return dto;
        }).toList();
    }

    private String resolveStatus(Long testId, Long userId) {
        if (userId == null) return "new";
        return resultRepository.existsByUserIdAndTestId(userId, testId) ? "done" : "new";
    }

    // ── GET /api/tests/{testId} ───────────────────────────
    public TestFullDTO getFullTest(Long testId) {
        Test test = testRepository.findById(testId).orElseThrow();
        List<Question> questions = questionRepository.findByTestId(testId);

        TestFullDTO dto = new TestFullDTO();
        dto.id   = test.getId();
        dto.name = test.getName();
        dto.time = test.getTimeMinutes();

        dto.questions = questions.stream().map(q -> {
            QuestionDTO qdto = new QuestionDTO();
            qdto.id       = q.getId();
            qdto.question = q.getQuestionText();
            qdto.answers  = answerRepository.findByQuestionId(q.getId()).stream().map(a -> {
                AnswerDTO adto = new AnswerDTO();
                adto.id   = a.getId();
                adto.text = a.getAnswerText();
                return adto;
            }).toList();
            return qdto;
        }).toList();

        return dto;
    }

    // ── POST /api/tests/{testId}/submit ──────────────────
    public ResultDTO submit(Long testId, SubmitRequest request, Long userId) {
        int correct = 0;
        for (UserAnswer ua : request.answers) {
            Answer answer = answerRepository.findById(ua.answerId).orElse(null);
            if (answer != null && answer.isCorrect()) correct++;
        }

        int    total      = request.answers.size();
        double percentage = total > 0 ? (correct * 100.0) / total : 0;

        UserResult result = new UserResult();
        result.setUserId(userId);
        result.setScore(correct);
        result.setTotal(total);
        result.setPercentage(percentage);
        result.setTimeSpent(request.timeSpent);
        result.setTest(testRepository.findById(testId).orElseThrow());
        UserResult saved = resultRepository.save(result);

        Test test = saved.getTest();
        ResultDTO dto = new ResultDTO();
        dto.id         = saved.getId();
        dto.score      = correct;
        dto.total      = total;
        dto.percentage = percentage;
        dto.timeSpent  = request.timeSpent;
        dto.testName   = test.getName();
        dto.finishedAt = saved.getFinishedAt();
        return dto;
    }

    // ── Word (.docx) import ───────────────────────────────
    // Format:
    //   Savol matni
    //   1. Javob A
    //   2. Javob B *    ← to'g'ri javob
    //   3. Javob C
    //   4. Javob D
    //   (bo'sh qator — keyingi savol)
    public int importFromWord(Long testId, MultipartFile file) throws Exception {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test topilmadi: " + testId));

        XWPFDocument doc = new XWPFDocument(file.getInputStream());
        List<String> lines = new ArrayList<>();
        for (XWPFParagraph p : doc.getParagraphs()) {
            lines.add(p.getText().trim());
        }
        doc.close();

        int imported = 0;
        int i = 0;

        while (i < lines.size()) {
            // Bo'sh qatorlarni o'tkazib yuborish
            while (i < lines.size() && lines.get(i).isEmpty()) i++;
            if (i >= lines.size()) break;

            // Savol matni
            String questionText = lines.get(i++);
            if (questionText.isEmpty()) continue;

            // Javoblarni yig'ish
            List<String> answerTexts   = new ArrayList<>();
            List<Boolean> correctFlags = new ArrayList<>();

            while (i < lines.size() && !lines.get(i).isEmpty()) {
                String raw = lines.get(i++);
                boolean correct = raw.contains("*");
                // "1. Javob A *" → "Javob A"
                String cleaned = raw.replaceAll("^\\d+\\.\\s*", "").replace("*", "").trim();
                if (!cleaned.isEmpty()) {
                    answerTexts.add(cleaned);
                    correctFlags.add(correct);
                }
            }

            if (answerTexts.isEmpty()) continue;

            // Agar hech to'g'ri javob belgilanmagan bo'lsa — birinchisini to'g'ri deb olamiz
            boolean anyCorrect = correctFlags.stream().anyMatch(c -> c);
            if (!anyCorrect && !correctFlags.isEmpty()) correctFlags.set(0, true);

            // Saqlash
            Question q = new Question();
            q.setQuestionText(questionText);
            q.setTest(test);
            questionRepository.save(q);

            for (int j = 0; j < answerTexts.size(); j++) {
                Answer a = new Answer();
                a.setAnswerText(answerTexts.get(j));
                a.setCorrect(correctFlags.get(j));
                a.setQuestion(q);
                answerRepository.save(a);
            }

            imported++;
        }

        return imported;
    }
}