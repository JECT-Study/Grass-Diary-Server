package chzzk.grassdiary.domain.diary.dto;

public class DiarySaveResponseDTO {
    public Long diaryId;
    public int rewardPoint;

    public DiarySaveResponseDTO(Long diaryId, int rewardPoint) {
        this.diaryId = diaryId;
        this.rewardPoint = rewardPoint;
    }
}
