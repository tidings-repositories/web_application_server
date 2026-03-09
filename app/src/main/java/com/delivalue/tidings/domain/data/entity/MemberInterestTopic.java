package com.delivalue.tidings.domain.data.entity;

import com.delivalue.tidings.domain.data.entity.embed.MemberTopicId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 기능 15: 사용자-토픽 관심도 (InterestedIn) — SimClusters InterestedIn (DOC_04)
 */
@Entity
@Table(name = "member_interest_topic")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberInterestTopic {

    @EmbeddedId
    private MemberTopicId id;

    // InterestedIn 임베딩 점수 (0.0~1.0)
    @Builder.Default
    @Column(name = "interest_score")
    private Double interestScore = 0.0;

    // 관심도 점수 산출 기반 (EXPLICIT=명시적 팔로우, IMPLICIT=행동 기반)
    @Column(name = "source_type", length = 20)
    @Builder.Default
    private String sourceType = "IMPLICIT";

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
