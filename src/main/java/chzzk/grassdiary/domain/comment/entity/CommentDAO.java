package chzzk.grassdiary.domain.comment.entity;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentDAO extends JpaRepository<Comment, Long> {
    @Query("""
    select c 
    from Comment c 
    join fetch c.member 
    left join fetch c.parentComment 
    where c.diary.id = :diaryId 
    order by c.parentComment.id asc nulls first, c.id asc
    """)
    List<Comment> findAllByDiaryId(Long diaryId, Pageable pageable);
}