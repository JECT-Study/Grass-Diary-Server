package chzzk.grassdiary.domain.image.dto;


import chzzk.grassdiary.domain.image.entity.DiaryToImage;

public record ImageDTO (
        Long imageId,
        String imageURL
) {
    private static String baseURL = "https://chzzk-image-server-dev.s3.ap-northeast-2.amazonaws.com/";

    public static ImageDTO from(DiaryToImage diaryToImage) {
        return new ImageDTO(diaryToImage.getImage().getId(), baseURL + diaryToImage.getImage().getImagePath());
    }
}
