package com.example.demo.rep;

import com.example.demo.model.UserResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResultRepository extends JpaRepository<UserResult, Long> {

    // Foydalanuvchi barcha natijalari
    List<UserResult> findByUserIdOrderByFinishedAtDesc(Long userId);

    // Foydalanuvchining bitta test natijalari
    List<UserResult> findByUserIdAndTestIdOrderByFinishedAtDesc(Long userId, Long testId);

    // ADMIN — native query: test_id bo'yicha, user_id ham birga keladi
    @Query(value = "SELECT * FROM user_results WHERE test_id = :testId ORDER BY percentage DESC",
            nativeQuery = true)
    List<UserResult> findByTestId(@Param("testId") Long testId);

    // Status tekshiruvi
    boolean existsByUserIdAndTestId(Long userId, Long testId);

    // Dashboard stats
    @Query(value = """
        SELECT
          COUNT(*)                        AS totalTests,
          AVG(score * 100.0 / total)      AS avgScore,
          MAX(score * 100.0 / total)      AS bestScore,
          SUM(time_spent)                 AS totalTimeSpent
        FROM user_results
        WHERE user_id = :userId
        """, nativeQuery = true)
    List<Object[]> getStatsByUserId(@Param("userId") Long userId);

    void deleteByTestId(Long testId);
    
}
