package chzzk.grassdiary.domain.diary.dto;

import chzzk.grassdiary.domain.color.ConditionLevel;
import chzzk.grassdiary.domain.diary.entity.Diary;
import chzzk.grassdiary.domain.member.entity.Member;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiarySaveRequestDTO {
    private Member member;
    private String content;
    private Boolean isPrivate;
    private Boolean hasTag;
    private ConditionLevel conditionLevel;
    private List<String> hashtags;

    // DTO -> Entity
    public Diary toEntity(Member member, Boolean hasImage) {
        return Diary.builder()
                .member(member)
                .content(content)
                .isPrivate(isPrivate)
                .hasImage(hasImage)
                .hasTag(hasTag)
                .conditionLevel(conditionLevel)
                .build();
    }
}
