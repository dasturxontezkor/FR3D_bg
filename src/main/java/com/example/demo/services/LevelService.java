package com.example.demo.services;

import com.example.demo.dto.LevelDTO;
import com.example.demo.model.Level;
import com.example.demo.rep.LevelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LevelService {

    private final LevelRepository levelRepository;

    public LevelService(LevelRepository levelRepository) {
        this.levelRepository = levelRepository;
    }

    // GET /api/levels → barcha levellar
    public List<LevelDTO> getAllLevels() {
        return levelRepository.findAll().stream().map(l -> {
            LevelDTO dto = new LevelDTO();
            dto.id          = l.getId();
            dto.name        = l.getName();
            dto.description = l.getDescription();
            return dto;
        }).toList();
    }
}