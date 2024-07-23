package chzzk.grassdiary.domain.diary.service;

import chzzk.grassdiary.domain.image.service.DiaryImageService;
import chzzk.grassdiary.domain.diary.dto.*;
import chzzk.grassdiary.domain.diary.entity.Diary;
import chzzk.grassdiary.domain.diary.entity.DiaryLike;
import chzzk.grassdiary.domain.diary.entity.DiaryLikeDAO;
import chzzk.grassdiary.domain.diary.entity.DiaryDAO;
import chzzk.grassdiary.domain.diary.entity.tag.DiaryTag;
import chzzk.grassdiary.domain.diary.entity.tag.DiaryTagDAO;
import chzzk.grassdiary.domain.diary.entity.tag.MemberTags;
import chzzk.grassdiary.domain.diary.entity.tag.MemberTagsDAO;
import chzzk.grassdiary.domain.diary.entity.tag.TagList;
import chzzk.grassdiary.domain.diary.entity.tag.TagListDAO;
import chzzk.grassdiary.domain.member.entity.Member;
import chzzk.grassdiary.domain.member.entity.MemberDAO;
import chzzk.grassdiary.domain.reward.RewardHistory;
import chzzk.grassdiary.domain.reward.RewardHistoryDAO;
import chzzk.grassdiary.domain.reward.RewardType;
import chzzk.grassdiary.global.common.error.exception.SystemException;
import chzzk.grassdiary.global.common.response.ClientErrorCode;
import chzzk.grassdiary.global.common.response.ServerErrorCode;
import chzzk.grassdiary.global.util.file.FileFolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryDAO diaryDAO;
    private final DiaryLikeDAO diaryLikeDAO;
    private final TagListDAO tagListDAO;
    private final MemberTagsDAO memberTagsDAO;
    private final MemberDAO memberDAO;
    private final DiaryTagDAO diaryTagDAO;
    private final RewardHistoryDAO rewardHistoryDAO;

    private final DiaryImageService diaryImageService;

    @Transactional
    public DiarySaveResponseDTO save(Long id, DiarySaveRequestDTO requestDto, MultipartFile image) {
        Member member = getMemberById(id);
        Diary diary = saveDiary(requestDto, member, image != null);
        saveTags(requestDto.getHashtags(), member, diary);

        int rewardPoint = makeRewardPoint();
        saveRewardPointAndHistory(member, rewardPoint);

        if (image == null) {
            return new DiarySaveResponseDTO(diary.getId(), false, "");
        }
        return sendSaveResponseWithImage(diary, image);
    }

    private DiarySaveResponseDTO sendSaveResponseWithImage(Diary diary, MultipartFile image) {
        String imagePath = diaryImageService.uploadDiaryImage(image, FileFolder.PERSONAL_DIARY, diary);
        return new DiarySaveResponseDTO(diary.getId(), true, imagePath);
    }

    @Transactional
    public DiarySaveResponseDTO update(Long id, DiaryUpdateRequestDTO requestDto, MultipartFile image) {
        Diary diary = getDiaryById(id);
        validateUpdateDate(diary);
        updateTags(diary, requestDto.getHashtags());

        boolean currentHasImage = requestDto.getHasImage();
        String imagePath = updateDiaryImage(currentHasImage, image, diary);

        return updateDiary(requestDto, diary, currentHasImage, imagePath);
    }

    /**
     * diaryId를 이용해서 diaryTag, MemberTag 를 찾아내기 diaryTag 삭제 -> deleteAllInBatch 고려해보기 MemberTag 삭제 해당 일기의 좋아요 찾기 및 삭제
     * 이미지 삭제
     */
    @Transactional
    public void delete(Long diaryId) {
        Diary diary = getDiaryById(diaryId);
        removeExistingTags(diary);
        removeDiaryLikes(diaryId);
        removeDiaryImage(diary);

        diaryDAO.delete(diary);
    }

    @Transactional(readOnly = true)
    public DiaryDetailDTO findById(Long diaryId, Long logInMemberId) {
        Diary diary = getDiaryById(diaryId);
        List<TagList> tags = getTagsByDiary(diaryId);
        boolean isLikedByLoginMember = isDiaryLikedByLoginMember(diaryId, logInMemberId);

        return DiaryDetailDTO.from(diary, tags, isLikedByLoginMember, getImageURL(diary.getHasImage(), diary.getId()));
    }

    @Transactional(readOnly = true)
    public Page<DiaryDetailDTO> findAll(Pageable pageable, Long memberId, Long logInMemberId) {
        getMemberById(memberId);

        return diaryDAO.findDiaryByMemberId(memberId, pageable)
                .map(diary -> {
                    List<TagList> tags = getTagsByDiary(diary.getId());
                    String imageURL = getImageURL(diary.getHasImage(), diary.getId());
                    boolean isLiked = isDiaryLikedByLoginMember(diary.getId(), logInMemberId);
                    return DiaryDetailDTO.from(diary, tags, isLiked, imageURL);
                });
    }

    @Transactional(readOnly = true)
    public DiaryDetailDTO findByDate(Long id, String date, Long logInMemberId) {
        LocalDate localDate = LocalDate.parse(date);
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);

        List<Diary> diaryList = diaryDAO.findByMemberIdAndCreatedAtBetween(id, startOfDay, endOfDay);
        if (diaryList.size() != 1) {
            // TODO: 일기를 찾지 못한 경우 보내는 값 설정 해야함
            return null;
        }

        Diary diary = diaryList.get(0);
        List<TagList> tags = getTagsByDiary(diary.getId());
        boolean isLikedByLogInMember = isDiaryLikedByLoginMember(diary.getId(), logInMemberId);

        return DiaryDetailDTO.from(
                diary,
                tags,
                isLikedByLogInMember,
                getImageURL(diary.getHasImage(), diary.getId())
        );
    }

    @Transactional
    public Long addLike(Long diaryId, Long memberId) {
        Diary diary = getDiaryById(diaryId);
        Member member = getMemberById(memberId);

        validateDiaryLike(diaryId, memberId);

        DiaryLike diaryLike = DiaryLike.builder()
                .diary(diary)
                .member(member)
                .build();
        diaryLikeDAO.save(diaryLike);

        // 추후 DTO로 return값 변경
        return diaryId;
    }

    @Transactional
    public Long deleteLike(Long diaryId, Long memberId) {
        getDiaryById(diaryId);

        DiaryLike diaryLike = diaryLikeDAO.findByDiaryIdAndMemberId(diaryId, memberId)
                .orElseThrow(() -> new SystemException(ClientErrorCode.DIARY_LIKE_NOT_FOUND));
        diaryLikeDAO.delete(diaryLike);

        // 추후 DTO로 return값 변경
        return diaryId;
    }

    private Member getMemberById(Long id) {
        return memberDAO.findById(id)
                .orElseThrow(() -> new SystemException(ClientErrorCode.MEMBER_NOT_FOUND_ERR));
    }

    private Diary getDiaryById(Long id) {
        return diaryDAO.findById(id)
                .orElseThrow(() -> new SystemException(ClientErrorCode.DIARY_NOT_FOUND_ERR));
    }

    private Diary saveDiary(DiarySaveRequestDTO requestDto, Member member, Boolean hasImage) {
        return diaryDAO.save(requestDto.toEntity(member, hasImage));
    }

    private void saveTags(List<String> hashtags, Member member, Diary diary) {
        if (hashtags != null) {
            hashtags.forEach(hashtag -> saveTag(hashtag, member, diary));
        }
    }

    private void saveTag(String hashtag, Member member, Diary diary) {
        TagList tagList = tagListDAO.findByTag(hashtag)
                .orElseGet(() -> tagListDAO.save(new TagList(hashtag)));
        tagList.incrementCount();
        MemberTags memberTags = memberTagsDAO.findByMemberIdAndTagList(member.getId(), tagList)
                .orElseGet(() -> memberTagsDAO.save(new MemberTags(member, tagList)));
        memberTags.incrementCount();
        diaryTagDAO.save(new DiaryTag(diary, memberTags));
    }

    private void updateTags(Diary diary, List<String> newHashtags) {
        removeExistingTags(diary);
        saveTags(newHashtags, diary.getMember(), diary);
    }

    private int makeRewardPoint() {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        return 50 + (random.nextInt(6) * 10);
    }

    private void saveRewardPointAndHistory(Member member, int rewardPoint) {
        member.addRandomPoint(rewardPoint);
        RewardHistory rewardHistory = rewardHistoryDAO
                .save(new RewardHistory(member, RewardType.PLUS_DIARY_WRITE, rewardPoint));

        if (rewardHistory.getId() == null) {
            throw new SystemException(ServerErrorCode.REWARD_HISTORY_SAVE_FAILED);
        }
    }

    private void validateUpdateDate(Diary diary) {
        LocalDate createdAt = diary.getCreatedAt().toLocalDate();
        LocalDate today = LocalDateTime.now().toLocalDate();
        if (!createdAt.equals(today)) {
            throw new SystemException(ClientErrorCode.DIARY_ALREADY_EXISTS);
        }
    }

    private void validateDiaryLike(Long diaryId, Long memberId) {
        diaryLikeDAO.findByDiaryIdAndMemberId(diaryId, memberId)
                .ifPresent(diaryLike -> {
                    throw new SystemException(ClientErrorCode.DIARY_LIKE_ALREADY_EXISTS);
                });
    }

    private void removeExistingTags(Diary diary) {
        List<DiaryTag> diaryTags = diaryTagDAO.findAllByDiaryId(diary.getId());
        List<MemberTags> memberTags = new ArrayList<>();
        List<TagList> tags = new ArrayList<>();
        for (DiaryTag diaryTag : diaryTags) {
            memberTags.add(diaryTag.getMemberTags());
            tags.add(diaryTag.getMemberTags().getTagList());
        }
        diaryTagDAO.deleteAll(diaryTags);
        decrementMemberTagsCount(memberTags);
        decrementTagsCount(tags);
    }

    private void decrementMemberTagsCount(List<MemberTags> memberTags) {
        for (MemberTags memberTag : memberTags) {
            memberTag.decrementCount();
            if (memberTag.getMemberTagUsageCount() == 0) {
                memberTagsDAO.delete(memberTag);
            }
        }
    }

    private void decrementTagsCount(List<TagList> tags) {
        for (TagList tag : tags) {
            tag.decrementCount();
            if (tag.getTagUsageCount() == 0) {
                tagListDAO.delete(tag);
            }
        }
    }

    private void removeDiaryLikes(Long diaryId) {
        List<DiaryLike> diaryLikes = diaryLikeDAO.findAllByDiaryId(diaryId);
        diaryLikeDAO.deleteAll(diaryLikes);
    }

    private void removeDiaryImage(Diary diary) {
        if (diary.getHasImage() != null && diary.getHasImage()) {
            diaryImageService.deleteImage(diary);
        }
    }

    private String updateDiaryImage(boolean currentHasImage, MultipartFile image, Diary diary) {
        boolean originalHasImage = Boolean.TRUE.equals(diary.getHasImage());
        if (originalHasImage) {
            diaryImageService.deleteImage(diary);
        }
        if (currentHasImage) {
            return diaryImageService.updateImage(originalHasImage, image, FileFolder.PERSONAL_DIARY, diary);
        }
        return "";
    }

    private DiarySaveResponseDTO updateDiary(DiaryUpdateRequestDTO requestDto, Diary diary, boolean currentHasImage,
                                             String imagePath) {
        diary.update(requestDto.getContent(), requestDto.getIsPrivate(), requestDto.getHasImage(),
                requestDto.getHasTag(), requestDto.getConditionLevel());

        if (currentHasImage) {
            return new DiarySaveResponseDTO(diary.getId(), true, imagePath);
        }
        return new DiarySaveResponseDTO(diary.getId(), false, "");
    }

    // 다이어리에 해당하는 태그 리스트
    private List<TagList> getTagsByDiary(Long diaryId) {
        List<DiaryTag> diaryTags = diaryTagDAO.findAllByDiaryId(diaryId);
        List<TagList> tags = new ArrayList<>();
        for (DiaryTag diaryTag : diaryTags) {
            tags.add(diaryTag.getMemberTags().getTagList());
        }
        return tags;
    }

    private boolean isDiaryLikedByLoginMember(Long diaryId, Long logInMemberId) {
        return diaryLikeDAO.findByDiaryIdAndMemberId(diaryId, logInMemberId).isPresent();
    }

    private String getImageURL(Boolean hasImage, Long diaryId) {
        if (hasImage != null && hasImage) {
            return diaryImageService.getImageURL(diaryId);
        }
        return "";
    }
}
