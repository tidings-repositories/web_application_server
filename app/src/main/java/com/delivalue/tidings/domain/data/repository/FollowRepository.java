package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.Follow;
import com.delivalue.tidings.domain.data.entity.Member;
import com.delivalue.tidings.domain.data.entity.embed.FollowId;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, FollowId> {

    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    @Query("SELECT m FROM Member m WHERE m.id IN (" + "SELECT f.id.followingId FROM Follow f WHERE f.id.followerId = :id)")
    List<Member> findFollowingMemberById(@Param("id") String id);

    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    @Query("SELECT m FROM Member m WHERE m.id IN (" + "SELECT f.id.followerId FROM Follow f WHERE f.id.followingId = :id)")
    List<Member> findFollowerMemberById(@Param("id") String id);
}
