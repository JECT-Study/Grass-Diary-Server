package chzzk.grassdiary.domain.comment.dto;

import chzzk.grassdiary.domain.comment.entity.Comment;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public record CommentResponseDTO(
        // 멤버 정보(사진, 아이디), 댓글내용, 작성 시간
        // todo: 사진정보 추가
        Long commentId,
        Long memberId,
        String content,
        boolean deleted,
        String createdDate,
        String createdAt,
        int depth,
        List<CommentResponseDTO> childComments
) {
    public static CommentResponseDTO from(Comment comment) {
        return new CommentResponseDTO(
                comment.getId(),
                comment.getMember().getId(),
                comment.getContent(),
                comment.isDeleted(),
                comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yy년 MM월 dd일")),
                comment.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm")),
                comment.getDepth(),
                comment.getChildComments().stream().map(CommentResponseDTO::fromComment).collect(Collectors.toList())
        );
    }

    public static CommentResponseDTO fromDeleted(Comment comment) {
        return new CommentResponseDTO(
                comment.getId(),
                null,
                null,
                true,
                null,
                null,
                comment.getDepth(),
                comment.getChildComments().stream().map(CommentResponseDTO::fromComment).collect(Collectors.toList())
        );
    }

    public static CommentResponseDTO fromComment(Comment comment) {
        if (comment.isDeleted()) {
            return fromDeleted(comment);
        } else {
            return from(comment);
        }
    }
}
