package chzzk.grassdiary.domain.diary.service;

import chzzk.grassdiary.domain.diary.entity.question.TodayQuestion;
import chzzk.grassdiary.domain.diary.entity.question.TodayQuestionDAO;
import chzzk.grassdiary.domain.diary.dto.TodayQuestionDTO;
import chzzk.grassdiary.global.common.error.exception.SystemException;
import chzzk.grassdiary.global.common.response.ServerErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static chzzk.grassdiary.domain.diary.entity.question.QuestionPrompt.getRandomQuestion;

@RequiredArgsConstructor
@Service
@Slf4j
public class TodayQuestionService {

    private final TodayQuestionDAO todayQuestionDAO;

    @Scheduled(cron = "0 0 0 * * *")
    @CacheEvict(value = "todayQuestion", allEntries = true) // 캐시 삭제: 모든 캐시 엔트리 삭제 
    @Transactional
    public void makeTodayQuestions() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        // 오늘 날짜의 질문이 이미 존재하는지 확인
        if (todayQuestionDAO.existsByCreatedAtBetween(startOfDay, endOfDay)) {
            log.info("오늘의 질문이 이미 존재합니다. 새로운 질문을 생성하지 않습니다.");
            return;
        }

        // 오늘의 질문이 없는 경우에만 새로운 질문 생성
        TodayQuestion newQuestion = new TodayQuestion(getRandomQuestion());
        todayQuestionDAO.save(newQuestion);
        log.info("새로운 오늘의 질문이 생성되었습니다: {}", newQuestion.getQuestionPrompt().getQuestion());
    }

    @Cacheable(value = "todayQuestion", key = "#root.method.name") // 캐시 저장: 메서드 이름을 키로 사용
    @Transactional(readOnly = true)
    public TodayQuestionDTO getTodayQuestion() {
        LocalDate today = LocalDate.now();

        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        return todayQuestionDAO.findLatestByCreatedAtBetween(startOfDay, endOfDay)
                .map(question -> new TodayQuestionDTO(question.getQuestionPrompt().getQuestion()))
                .orElseThrow(() -> new SystemException(ServerErrorCode.QUESTION_UNAVAILABLE));
    }
}
