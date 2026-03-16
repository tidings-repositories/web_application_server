package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.MemberGameProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberGameProfileRepository extends JpaRepository<MemberGameProfile, Long> {

    List<MemberGameProfile> findAllByMemberId(String memberId);

    List<MemberGameProfile> findAllByMemberIdAndPlayType(String memberId, String playType);
}
