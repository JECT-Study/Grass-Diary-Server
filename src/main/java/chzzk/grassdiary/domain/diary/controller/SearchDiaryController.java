package chzzk.grassdiary.domain.diary.controller;

import chzzk.grassdiary.domain.diary.service.DiaryService;
import chzzk.grassdiary.domain.diary.service.TagService;
import chzzk.grassdiary.domain.diary.dto.DiaryDetailDTO;
import chzzk.grassdiary.domain.diary.dto.TagDTO;
import chzzk.grassdiary.global.auth.common.AuthenticatedMember;
import chzzk.grassdiary.global.auth.service.dto.AuthMemberPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/search")
@Tag(name = "검색 컨트롤러")
public class SearchDiaryController {
    private final TagService tagService;
    private final DiaryService diaryService;

    @GetMapping("hashTag/{memberId}")
    @Operation(
            summary = "유저가 사용한 해시태그 리스트",
            description = "")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = TagDTO.class)))
    @Parameter(name = "memberId", description = "멤버 아이디")
    public ResponseEntity<?> searchHashTag(@PathVariable Long memberId) {
        return ResponseEntity.ok(tagService.getMemberTags(memberId));
    }

    @GetMapping("tagId/{memberId}")
    @Operation(
            summary = "해시태그에 대한 다이어리 검색",
            description = "유저가 해시태그를 선택하면 해당 해시태그를 사용한 다이어리 리스트를 반환")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = DiaryDetailDTO.class)))
    @Parameters({
            @Parameter(name = "memberId", description = "멤버 아이디"),
            @Parameter(name = "tagId", description = "검색을 원하는 해시태그 아이디")
    })
    public ResponseEntity<?> findByHashTagId(
            @PathVariable Long memberId,
            @RequestParam Long tagId,
            @AuthenticatedMember AuthMemberPayload payload
    ) {
        return ResponseEntity.ok(tagService.findByHashTagId(memberId, tagId, payload.id()));
    }

    @GetMapping("date/{memberId}")
    @Operation(
            summary = "날짜별 일기 검색",
            description = "유저별 날짜에 따른 일기 검색(잔디 클릭시 사용)")
    @Parameters({
            @Parameter(name = "memberId", description = "멤버 아이디"),
            @Parameter(name = "date", description = "검색하는 날짜 String 값(형식: yyyy-MM-dd)")
    })
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = DiaryDetailDTO.class)))
    public ResponseEntity<?> searchByDate(
            @PathVariable Long memberId,
            @RequestParam String date,
            @AuthenticatedMember AuthMemberPayload payload
    ) {
        return ResponseEntity.ok(diaryService.findByDate(memberId, date, payload.id()));
    }
}
