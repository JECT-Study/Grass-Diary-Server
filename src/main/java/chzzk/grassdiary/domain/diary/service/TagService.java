package chzzk.grassdiary.domain.diary.service;

import chzzk.grassdiary.domain.diary.entity.Diary;
import chzzk.grassdiary.domain.diary.entity.DiaryLikeDAO;
import chzzk.grassdiary.domain.diary.entity.tag.DiaryTagDAO;
import chzzk.grassdiary.domain.diary.entity.tag.MemberTags;
import chzzk.grassdiary.domain.diary.entity.tag.MemberTagsDAO;
import chzzk.grassdiary.domain.diary.entity.tag.TagList;
import chzzk.grassdiary.domain.diary.entity.tag.TagListDAO;
import chzzk.grassdiary.domain.diary.dto.DiaryDetailDTO;
import chzzk.grassdiary.domain.diary.dto.TagDTO;
import java.util.List;
import java.util.stream.Collectors;

import chzzk.grassdiary.domain.image.service.DiaryImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TagService {

    private final MemberTagsDAO memberTagsDAO;
    private final TagListDAO tagListDAO;
    private final DiaryTagDAO diaryTagDAO;
    private final DiaryLikeDAO diaryLikeDAO;

    private final DiaryImageService diaryImageService;

    /**
     * 유저의 해시태그 리스트 반환
     */
    public List<TagDTO> getMemberTags(Long memberId) {
        // 멤버가 사용한 태그의 tag_id 목록 가져오기
        List<MemberTags> memberTags = memberTagsDAO.findMemberTagsByMemberId(memberId);

        return memberTags.stream().map(tag ->
                new TagDTO(
                        tag.getTagList().getId(),
                        tag.getTagList().getTag(),
                        tag.getMemberTagUsageCount())
        ).toList();
    }

    /**
     * 유저의 다이어리 태그로 다이어리 검색
     */
    public List<DiaryDetailDTO> findByHashTagId(Long memberId, Long tagId, Long logInMemberId) {
        List<Diary> diaries = diaryTagDAO.findByMemberIdAndTagId(memberId, tagId);

        return diaries.stream()
                .map(diary -> {
                    List<MemberTags> diaryTags = diaryTagDAO.findMemberTagsByDiaryId(diary.getId());
                    List<TagList> tags = diaryTags.stream()
                            .map(MemberTags::getTagList)
                            .toList();
                    boolean isLiked = diaryLikeDAO.findByDiaryIdAndMemberId(diary.getId(), logInMemberId).isPresent();

                    return DiaryDetailDTO.from(diary, tags, isLiked, getImageURL(diary.getHasImage(), diary.getId()));
                })
                .toList();
    }

    private String getImageURL(Boolean hasImage, Long diaryId) {
        if (hasImage != null && hasImage) {
            return diaryImageService.getImageURL(diaryId);
        }
        return "";
    }
}
