package chzzk.grassdiary.domain.diary.entity.question;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TodayQuestionDAO extends JpaRepository<TodayQuestion, Long> {
    @Query("SELECT t FROM TodayQuestion t WHERE t.createdAt >= :startOfDay AND t.createdAt < :endOfDay ORDER BY t.createdAt DESC LIMIT 1")
    Optional<TodayQuestion> findLatestByCreatedAtBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

    boolean existsByCreatedAtBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
}
