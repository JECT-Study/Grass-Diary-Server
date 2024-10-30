package chzzk.grassdiary.domain.comment.dto;

import chzzk.grassdiary.domain.comment.entity.Comment;
import chzzk.grassdiary.domain.diary.entity.Diary;
import chzzk.grassdiary.domain.member.entity.Member;

public record CommentSaveRequestDTO(
        String content,
        Long parentCommentId
) {
    public Comment toEntity(Member member, Diary diary, Comment parentComment, int depth) {
        return Comment.builder()
                .member(member)
                .diary(diary)
                .content(content)
                .parentComment(parentComment)
                .depth(depth)
                .build();
    }
}

