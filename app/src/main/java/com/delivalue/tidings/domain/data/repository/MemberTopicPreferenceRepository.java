package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.MemberTopicPreference;
import com.delivalue.tidings.domain.data.entity.embed.MemberTopicId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberTopicPreferenceRepository extends JpaRepository<MemberTopicPreference, MemberTopicId> {

    List<MemberTopicPreference> findAllByIdMemberId(String memberId);

    List<MemberTopicPreference> findAllByIdMemberIdAndPreferenceType(String memberId, String preferenceType);
}
