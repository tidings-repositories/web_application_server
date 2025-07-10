package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.MemberBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberBadgeRepository extends JpaRepository<MemberBadge, Integer> {

    List<MemberBadge> findByMemberId(String memberId);

    MemberBadge findByMemberIdAndBadge_Id(String memberId, Integer badgeId);
}
