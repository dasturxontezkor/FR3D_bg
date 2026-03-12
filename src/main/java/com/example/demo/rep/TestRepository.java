package com.example.demo.rep;

import com.example.demo.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findByLevelId(Long levelId);
}