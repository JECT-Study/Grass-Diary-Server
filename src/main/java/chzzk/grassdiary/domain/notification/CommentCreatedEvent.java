package chzzk.grassdiary.domain.notification;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
@Slf4j
public class CommentCreatedEvent extends ApplicationEvent {
    private final Long diaryId;
    private final String authorName;
    private final String diaryAuthorEmail;
    private final String commentContent;
    private final LocalDateTime diaryCreatedAt;
    private final String commentCreatedBy;
    private final LocalDateTime commentCreatedAt;

    public CommentCreatedEvent(Object source,
                               Long diaryId,
                               String authorName,
                               String diaryAuthorEmail,
                               String commentContent,
                               LocalDateTime diaryCreatedAt,
                               String commentCreatedBy,
                               LocalDateTime commentCreatedAt
    ) {
        super(source);
        this.diaryId = diaryId;
        this.authorName = authorName;
        this.diaryAuthorEmail = diaryAuthorEmail;
        this.commentContent = commentContent;
        this.diaryCreatedAt = diaryCreatedAt;
        this.commentCreatedBy = commentCreatedBy;
        this.commentCreatedAt = commentCreatedAt;
    }
}
