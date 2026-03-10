package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.MemberPlatformPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberPlatformPreferenceRepository extends JpaRepository<MemberPlatformPreference, Long> {

    List<MemberPlatformPreference> findAllByMemberIdOrderByPreferenceScoreDesc(String memberId);
}
