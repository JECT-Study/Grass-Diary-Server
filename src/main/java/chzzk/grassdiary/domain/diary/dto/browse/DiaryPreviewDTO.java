package chzzk.grassdiary.domain.diary.dto.browse;

import chzzk.grassdiary.domain.diary.entity.Diary;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

public record DiaryPreviewDTO(
        Long diaryId,
        String content,
        int diaryLikeCount,
        int commentCount,
        float transparency,
        Long memberId,
        String nickname,
        String createdAt) {

    public static List<DiaryPreviewDTO> of(Page<Diary> diaries) {
        return diaries.stream()
                .map(d -> new DiaryPreviewDTO(
                        d.getId(),
                        trimContent(d.getContent()),
                        d.getDiaryLikes().size(),
                        d.getComments().size(),
                        d.getConditionLevel().getTransparency(),
                        d.getMember().getId(),
                        d.getMember().getNickname(),
                        d.getCreatedAt()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                )).toList();
    }

    private static DiaryPreviewDTO toPreviewDiary(Diary diary) {
        return new DiaryPreviewDTO(
                diary.getId(),
                trimContent(diary.getContent()),
                diary.getDiaryLikes().size(),
                diary.getComments().size(),
                diary.getConditionLevel().getTransparency(),
                diary.getMember().getId(),
                diary.getMember().getNickname(),
                diary.getCreatedAt()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    public static List<DiaryPreviewDTO> toLatestDiariesDto(List<Diary> diaries) {
        return diaries.stream()
                .map(DiaryPreviewDTO::toPreviewDiary)
                .toList();
    }

    private static String trimContent(String content) {
        return Optional.ofNullable(content)
                .map(c -> c.length() > 250 ? c.substring(0, 250) + "..." : c)
                .orElse("");
    }

}
