package com.example.demo.services;

import com.example.demo.dto.ResultDTO;
import com.example.demo.dto.StatsDTO;
import com.example.demo.model.UserResult;
import com.example.demo.rep.ResultRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResultService {

    private final ResultRepository resultRepository;

    public ResultService(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    // GET /api/results/my
    public List<ResultDTO> getMyResults(Long userId) {
        return resultRepository.findByUserIdOrderByFinishedAtDesc(userId)
                .stream().map(this::toDTO).toList();
    }

    // GET /api/results/test/{testId}
    public List<ResultDTO> getResultsByTest(Long userId, Long testId) {
        return resultRepository.findByUserIdAndTestIdOrderByFinishedAtDesc(userId, testId)
                .stream().map(this::toDTO).toList();
    }

    // GET /api/results/stats
    public StatsDTO getStats(Long userId) {
        StatsDTO dto = new StatsDTO();

        List<Object[]> rows = resultRepository.getStatsByUserId(userId);

        // Agar natija yo'q yoki list bo'sh bo'lsa
        if (rows == null || rows.isEmpty()) {
            dto.totalTests     = 0;
            dto.avgScore       = 0;
            dto.bestScore      = 0;
            dto.totalTimeSpent = 0;
            return dto;
        }

        // JPA bitta qator qaytaradi — rows.get(0) bu Object[] massiv
        Object[] row = rows.get(0);

        dto.totalTests     = row[0] != null ? ((Number) row[0]).longValue()   : 0;
        dto.avgScore       = row[1] != null ? ((Number) row[1]).doubleValue() : 0;
        dto.bestScore      = row[2] != null ? ((Number) row[2]).doubleValue() : 0;
        dto.totalTimeSpent = row[3] != null ? ((Number) row[3]).longValue()   : 0;

        return dto;
    }

    private ResultDTO toDTO(UserResult r) {
        ResultDTO dto  = new ResultDTO();
        dto.id         = r.getId();
        dto.score      = r.getScore();
        dto.total      = r.getTotal();
        dto.percentage = r.getPercentage();
        dto.timeSpent  = r.getTimeSpent();
        dto.testName   = r.getTest() != null ? r.getTest().getName() : null;
        dto.finishedAt = r.getFinishedAt();
        return dto;
    }
}