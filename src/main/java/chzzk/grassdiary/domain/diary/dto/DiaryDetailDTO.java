package chzzk.grassdiary.domain.diary.dto;

import chzzk.grassdiary.domain.diary.entity.Diary;
import chzzk.grassdiary.domain.diary.entity.tag.TagList;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record DiaryDetailDTO(
        Long diaryId,
        String content,
        List<TagList> tags,
        Float transparency,
        Boolean isPrivate,
        Integer likeCount,
        Boolean isLikedByLogInMember,
        String createdDate,
        String createdAt,
        Boolean hasImage,
        String imageURL
) {
    public static DiaryDetailDTO from(Diary diary, List<TagList> tags, boolean isLikedByLogInMember, String imageURL) {
        return new DiaryDetailDTO(
                diary.getId(),
                diary.getContent(),
                tags,
                diary.getConditionLevel().getTransparency(),
                diary.getIsPrivate(),
                diary.getDiaryLikes().size(),
                isLikedByLogInMember,
                diary.getCreatedAt().format(DateTimeFormatter.ofPattern("yy년 MM월 dd일")),
                diary.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm")),
                diary.getHasImage(),
                imageURL
        );
    }
}
