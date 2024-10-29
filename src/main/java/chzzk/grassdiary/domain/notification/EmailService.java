package chzzk.grassdiary.domain.notification;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private final SpringTemplateEngine templateEngine;

    public void sendCommentNotification(CommentCreatedEvent event) {
        try {
            log.info("Sending comment notification for comment {}", event);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("recipientName", event.getAuthorName());
            context.setVariable("diaryDate", event.getDiaryCreatedAt());
            context.setVariable("commenterName", event.getCommentCreatedBy());
            context.setVariable("commentDate", event.getCommentCreatedAt());
            context.setVariable("commentContent", event.getCommentContent());
            context.setVariable("diaryUrl", "https://grassdiary.site/diary/" + event.getDiaryId());
//            context.setVariable("unsubscribeUrl", "http://your-domain.com/unsubscribe");

            String htmlContent = templateEngine.process("comment-notification", context);

            helper.setTo(event.getDiaryAuthorEmail());
            helper.setSubject("[잔디 일기] 새로운 댓글 알림");
            helper.setText(htmlContent, true);

            ClassPathResource imageResource = new ClassPathResource("static/images/grass-diary-logo.png");
            helper.addInline("headerImage", imageResource);

            mailSender.send(message);
            log.info("{}님께 [댓글 알림] 이메일을 보냈습니다.", event.getAuthorName());

        } catch (MessagingException e) {
            throw new RuntimeException("댓글 이메일 보내기가 실패했습니다.", e);
        }
    }
}
