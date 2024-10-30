package chzzk.grassdiary.domain.comment.controller;

import chzzk.grassdiary.domain.comment.dto.CommentDeleteResponseDTO;
import chzzk.grassdiary.domain.comment.dto.CommentResponseDTO;
import chzzk.grassdiary.domain.comment.dto.CommentSaveRequestDTO;
import chzzk.grassdiary.domain.comment.dto.CommentUpdateRequestDTO;
import chzzk.grassdiary.domain.comment.service.CommentService;
import chzzk.grassdiary.global.auth.common.AuthenticatedMember;
import chzzk.grassdiary.global.auth.service.dto.AuthMemberPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
@Tag(name = "댓글 컨트롤러")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{diaryId}")
    @Operation(
            summary = "댓글 작성 요청",
            description = "댓글 내용, 부모 댓글이 있다면 id 값, 없다면 null")
    public CommentResponseDTO save(
            @PathVariable(name = "diaryId") Long diaryId,
            @RequestBody CommentSaveRequestDTO requestDTO,
            @Schema(hidden = true) @AuthenticatedMember AuthMemberPayload payload
    ) {
        return commentService.save(diaryId, requestDTO, payload.id());
    }

    @PatchMapping("/{commentId}")
    public CommentResponseDTO update(
            @PathVariable(name = "commentId") Long commentId,
            @RequestBody CommentUpdateRequestDTO requestDTO,
            @AuthenticatedMember AuthMemberPayload payload
    ) {
        return commentService.update(commentId, requestDTO, payload.id());
    }

    @PatchMapping("/{commentId}/delete")
    public CommentDeleteResponseDTO delete(
            @PathVariable(name = "commentId") Long commentId,
            @AuthenticatedMember AuthMemberPayload payload
    ) {
        return commentService.delete(commentId, payload.id());
    }

    // 모든 댓글 검색
    @GetMapping("/{diaryId}")
    public List<CommentResponseDTO> findAll(
            Pageable pageable,
            @PathVariable(name = "diaryId") Long diaryId
    ) {
        return commentService.findAll(pageable, diaryId);
    }
}
