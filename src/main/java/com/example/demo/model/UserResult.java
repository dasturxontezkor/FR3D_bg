package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_results")   // ← DB jadval nomi aniq ko'rsatildi
public class UserResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")   // ← aniq column nomi
    private Long userId;

    private int score;
    private int total;
    private double percentage;

    @Column(name = "time_spent")
    private int timeSpent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id")   // ← aniq FK nomi
    private Test test;

    private LocalDateTime finishedAt;

    @PrePersist
    protected void onCreate() {
        finishedAt = LocalDateTime.now();
    }

    public Long getId()                              { return id; }
    public void setId(Long id)                       { this.id = id; }

    public Long getUserId()                          { return userId; }
    public void setUserId(Long userId)               { this.userId = userId; }

    public int getScore()                            { return score; }
    public void setScore(int score)                  { this.score = score; }

    public int getTotal()                            { return total; }
    public void setTotal(int total)                  { this.total = total; }

    public double getPercentage()                    { return percentage; }
    public void setPercentage(double pct)            { this.percentage = pct; }

    public int getTimeSpent()                        { return timeSpent; }
    public void setTimeSpent(int timeSpent)          { this.timeSpent = timeSpent; }

    public Test getTest()                            { return test; }
    public void setTest(Test test)                   { this.test = test; }

    public LocalDateTime getFinishedAt()             { return finishedAt; }
    public void setFinishedAt(LocalDateTime dt)      { this.finishedAt = dt; }
}