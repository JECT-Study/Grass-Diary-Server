package chzzk.grassdiary.domain.diary.controller;

import chzzk.grassdiary.global.auth.common.AuthenticatedMember;
import chzzk.grassdiary.global.auth.service.dto.AuthMemberPayload;
import chzzk.grassdiary.domain.diary.service.BrowseDiaryService;
import chzzk.grassdiary.domain.diary.dto.browse.AllLatestDiariesDto;
import chzzk.grassdiary.domain.diary.dto.browse.DiaryPreviewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "공유 페이지 컨트롤러")
@RestController
@RequestMapping("/api/shared/diaries")
@RequiredArgsConstructor
public class BrowseDiaryController {
    private final BrowseDiaryService browseDiaryService;

    @GetMapping("/popular")
    @Operation(summary = "Top10 API", description = "이번 주에 공개된 일기 중 좋아요 순, 작성 순으로 10개의 일기를 보여준다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DiaryPreviewDTO.class))})
    })
    public ResponseEntity<List<DiaryPreviewDTO>> showTop10ThisWeek() {
        List<DiaryPreviewDTO> top10Diaries = browseDiaryService.findTop10DiariesThisWeek();
        return ResponseEntity.ok(top10Diaries);
    }

    @GetMapping("/latest")
    @Operation(summary = "최신순 일기 필드 API", description = "공개된 일기를 최신순으로 보여준다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AllLatestDiariesDto.class))})
    })
    public ResponseEntity<AllLatestDiariesDto> showLatestDiariesAfterCursor(
            @RequestParam(value = "cursorId", required = false, defaultValue = Long.MAX_VALUE + "") Long cursorId,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        AllLatestDiariesDto latestDiaries = browseDiaryService.findLatestDiariesAfterCursor(cursorId, size);
        return ResponseEntity.ok(latestDiaries);
    }
}
