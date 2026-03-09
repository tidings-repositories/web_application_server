package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.MemberInterestTopic;
import com.delivalue.tidings.domain.data.entity.embed.MemberTopicId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberInterestTopicRepository extends JpaRepository<MemberInterestTopic, MemberTopicId> {

    List<MemberInterestTopic> findAllByIdMemberIdOrderByInterestScoreDesc(String memberId);
}
