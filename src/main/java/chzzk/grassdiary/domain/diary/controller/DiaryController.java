package chzzk.grassdiary.domain.diary.controller;

import chzzk.grassdiary.domain.diary.dto.*;
import chzzk.grassdiary.global.auth.common.AuthenticatedMember;
import chzzk.grassdiary.global.auth.service.dto.AuthMemberPayload;
import chzzk.grassdiary.domain.diary.service.DiaryService;
import chzzk.grassdiary.domain.diary.service.TodayQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/diary")
@Tag(name = "일기 컨트롤러")
public class DiaryController {
    private final DiaryService diaryService;
    private final TodayQuestionService todayQuestionService;

    @PostMapping("/{memberId}")
    public DiarySaveResponseDTO save(
            @PathVariable(name = "memberId") Long memberId,
            @RequestPart DiarySaveRequestDTO requestDto,
            @RequestPart(required = false) MultipartFile image
    ) {
        return diaryService.save(memberId, requestDto, image);
    }

    @PatchMapping("/{diaryId}")
    public DiarySaveResponseDTO update(
            @PathVariable(name = "diaryId") Long diaryId,
            @RequestPart DiaryUpdateRequestDTO requestDto,
            @RequestPart(required = false) MultipartFile image
    ) {
        return diaryService.update(diaryId, requestDto, image);
    }

    @DeleteMapping("/{diaryId}")
    public Long delete(@PathVariable(name = "diaryId") Long diaryId) {
        diaryService.delete(diaryId);
        return diaryId;
    }

    @GetMapping("/{diaryId}")
    public DiaryDetailDTO findById(@PathVariable(name = "diaryId") Long diaryId,
                                   @AuthenticatedMember AuthMemberPayload payload) {
        return diaryService.findById(diaryId, payload.id());
    }

    /**
     * 유저별 일기장 메인 페이지 1) 디폴트 API(최신순 5개씩) : api/diary/main/1?page=0 2) 오래된 순 5개씩 :
     * api/diary/main/1?page=0&sort=createdAt,ASC
     */
    @GetMapping("/main/{memberId}")
    @Operation(
            summary = "유저별 일기장 메인 페이지",
            description =
                    "최신순 5개씩(`api/diary/main/{memberId}?page=0`), 오래된 순 5개씩(`api/diary/main/1?page=0&sort=createdAt,ASC`) "
                            + "\n페이지를 나타내는 변수가 있는데, 해당 표에는 바로 나타나지 않으니 실행 시켜보시는 것을 추천드립니다.")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = DiaryDetailDTO.class)))
    public ResponseEntity<?> findAll(
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable Long memberId,
            @AuthenticatedMember AuthMemberPayload payload
    ) {
        return ResponseEntity.ok(diaryService.findAll(pageable, memberId, payload.id()));
    }

    // todo: 로그인 공부 후 memberId 부분을 auth로? 대체할 수 있는지 알아보기
    @PostMapping("/like/{diaryId}/{memberId}")
    public Long addLike(@PathVariable("diaryId") Long diaryId, @PathVariable("memberId") Long memberId) {
        return diaryService.addLike(diaryId, memberId);
    }

    @DeleteMapping("/like/{diaryId}/{memberId}")
    public Long deleteLike(@PathVariable("diaryId") Long diaryId, @PathVariable("memberId") Long memberId) {
        return diaryService.deleteLike(diaryId, memberId);
    }
  
    @GetMapping("/today-question")
    @Operation(summary = "오늘의 질문")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = TodayQuestionDTO.class)))
    public ResponseEntity<?> findTodayQuestion() {
        return ResponseEntity.ok(todayQuestionService.getTodayQuestion());
    }
}
