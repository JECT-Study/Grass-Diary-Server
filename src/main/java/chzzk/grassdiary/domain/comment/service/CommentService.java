package chzzk.grassdiary.domain.comment.service;

import chzzk.grassdiary.domain.comment.dto.CommentDeleteResponseDTO;
import chzzk.grassdiary.domain.comment.dto.CommentSaveRequestDTO;
import chzzk.grassdiary.domain.comment.dto.CommentUpdateRequestDTO;
import chzzk.grassdiary.domain.comment.entity.Comment;
import chzzk.grassdiary.domain.comment.entity.CommentDAO;
import chzzk.grassdiary.domain.diary.entity.Diary;
import chzzk.grassdiary.domain.diary.entity.DiaryDAO;
import chzzk.grassdiary.domain.comment.dto.CommentResponseDTO;
import chzzk.grassdiary.domain.member.entity.Member;
import chzzk.grassdiary.domain.member.entity.MemberDAO;
import chzzk.grassdiary.global.common.error.exception.SystemException;
import chzzk.grassdiary.global.common.response.ClientErrorCode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentDAO commentDAO;
    private final DiaryDAO diaryDAO;
    private final MemberDAO memberDAO;

    @Transactional
    public CommentResponseDTO save(Long diaryId, CommentSaveRequestDTO requestDTO, Long logInMemberId) {
        Member member = getMemberById(logInMemberId);
        Diary diary = getDiaryById(diaryId);
        Comment parentComment = getParentCommentById(requestDTO.parentCommentId());

        int commentDepth = calculateCommentDepth(parentComment);
        validateCommentDepth(commentDepth);

        Comment comment = requestDTO.toEntity(member, diary, parentComment, commentDepth);
        commentDAO.saveAndFlush(comment);

        assignGroupToComment(comment, parentComment);

        return CommentResponseDTO.from(comment);
    }

    private void assignGroupToComment(Comment comment, Comment parentComment) {
        if (parentComment == null) {
            // For parent comments, group is its own ID
            comment.assignGroup(comment.getId());
        } else {
            // For child comments, group is the parent's group
            comment.assignGroup(parentComment.getGroup());
        }
    }

    @Transactional
    public CommentResponseDTO update(Long commentId, CommentUpdateRequestDTO requestDTO, Long logInMemberId) {
        Member member = getMemberById(logInMemberId);
        Comment comment = getCommentById(commentId);
        validateCommentAuthor(member, comment);
        comment.update(requestDTO.content());

        return CommentResponseDTO.from(comment);
    }

    @Transactional
    public CommentDeleteResponseDTO delete(Long commentId, Long logInMemberId) {
        Member member = getMemberById(logInMemberId);
        Comment comment = getCommentById(commentId);

        validateDeleteAuthorization(member, comment);
//        validateCommentAuthor(member, comment);
        validateNotDeleted(comment);

        comment.delete();

        return CommentDeleteResponseDTO.from(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDTO> findAll(Pageable pageable, Long diaryId) {
        Diary diary = getDiaryById(diaryId);
        List<Comment> comments = commentDAO.findAllByDiaryId(diaryId, pageable);

        List<Comment> hierarchicalComments = convertHierarchy(comments);

        return hierarchicalComments.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private List<Comment> convertHierarchy(List<Comment> comments) {
        Map<Long, Comment> map = new HashMap<>();
        List<Comment> parentComments = new ArrayList<>();

        for (Comment comment : comments) {
            if (comment.getParentComment() != null) {
                // 부모 댓글이 있는 경우
                Comment parentComment = map.get(comment.getParentComment().getId());
                if (parentComment != null) {
                    parentComment.getChildComments().add(comment);
                }
            } else {
                // 부모 댓글이 없는 경우
                parentComments.add(comment);
            }
            map.put(comment.getId(), comment);
        }

        return parentComments;
    }

    private CommentResponseDTO mapToDTO(Comment comment) {
        if (comment.isDeleted()) {
            return CommentResponseDTO.fromDeleted(comment);
        }
        return CommentResponseDTO.from(comment);
    }

    private Member getMemberById(Long id) {
        return memberDAO.findById(id)
                .orElseThrow(() -> new SystemException(ClientErrorCode.MEMBER_NOT_FOUND_ERR));
    }

    private Diary getDiaryById(Long id) {
        return diaryDAO.findById(id)
                .orElseThrow(() -> new SystemException(ClientErrorCode.DIARY_NOT_FOUND_ERR));
    }

    private Comment getParentCommentById(Long id) {
        if (id == null) {
            return null;
        }
        return getCommentById(id);
    }

    private Comment getCommentById(Long id) {
        return commentDAO.findById(id)
                .orElseThrow(() -> new SystemException(ClientErrorCode.COMMENT_NOT_FOUND_ERR));
    }

    private int calculateCommentDepth(Comment parentComment) {
        if (parentComment == null) {
            return 0;
        }
        return parentComment.getDepth() + 1;
    }

    private void validateCommentAuthor(Member member, Comment comment) {
        if (!member.equals(comment.getMember())) {
            throw new SystemException(ClientErrorCode.AUTHOR_MISMATCH_ERR);
        }
    }

    private void validateDeleteAuthorization(Member member, Comment comment) {
        Diary diary = comment.getDiary();

        boolean isAuthorized = member.equals(comment.getMember()) || member.equals(diary.getMember());

        if (!isAuthorized) {
            throw new SystemException(ClientErrorCode.AUTHOR_MISMATCH_ERR);
        }
    }

    private void validateNotDeleted(Comment comment) {
        if (comment.isDeleted()) {
            throw new SystemException(ClientErrorCode.COMMENT_ALREADY_DELETED_ERR);
        }
    }

    private void validateCommentDepth(int depth) {
        if (depth > 1) {
            throw new SystemException(ClientErrorCode.COMMENT_DEPTH_EXCEEDED_ERR);
        }
    }
}
