package chzzk.grassdiary.domain.diary.entity.tag;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTagsDAO extends JpaRepository<MemberTags, Long> {

    // 멤버가 사용한 태그 리스트 반환
    List<MemberTags> findMemberTagsByMemberId(Long memberId);

    // 멤버가 사용한 태그 반환(태그 유무 조회용)
    Optional<MemberTags> findByMemberIdAndTagList(Long memberId, TagList tagList);
}
