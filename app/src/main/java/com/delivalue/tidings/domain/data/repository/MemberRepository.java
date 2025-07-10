package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

    Optional<Member> findByIdAndDeletedAtIsNull(String id);

    Member findByPublicId(String publicId);

    @Modifying
    @Query("UPDATE Member m SET m.followingCount = m.followingCount + 1 WHERE m.id = :id")
    void increaseFollowingCount(@Param("id") String id);

    @Modifying
    @Query("UPDATE Member m SET m.followingCount = m.followingCount - 1 WHERE m.id = :id")
    void decreaseFollowingCount(@Param("id") String id);

    @Modifying
    @Query("UPDATE Member m SET m.followerCount = m.followerCount + 1 WHERE m.id = :id")
    void increaseFollowerCount(@Param("id") String id);

    @Modifying
    @Query("UPDATE Member m SET m.followerCount = m.followerCount - 1 WHERE m.id = :id")
    void decreaseFollowerCount(@Param("id") String id);

    @Query("""
    SELECT m FROM Member m
    WHERE (m.name LIKE CONCAT(:keyword, '%') OR m.publicId LIKE CONCAT(:keyword, '%'))
    AND m.deletedAt IS NULL
    """)
    List<Member> searchByKeyword(@Param("keyword") String keyword);
}
