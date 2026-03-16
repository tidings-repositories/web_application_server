package com.delivalue.tidings.domain.data.entity;

import com.delivalue.tidings.domain.data.entity.embed.UserPairId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 기능 3: RealGraph 사용자 쌍 상호작용 요약 — RealGraph InteractionGraphAggregationJob (DOC_06)
 * 기능 14: lingerCount, mentionCount, totalLingerTimeMs, daysSinceFirstInteraction 추가
 * 기능 35: interactionScoreExplicit 추가
 */
@Entity
@Table(name = "user_pair_interaction_summary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserPairInteractionSummary {

    @EmbeddedId
    private UserPairId id;

    @Builder.Default
    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Builder.Default
    @Column(name = "comment_count")
    private Integer commentCount = 0;

    @Builder.Default
    @Column(name = "repost_count")
    private Integer repostCount = 0;

    @Builder.Default
    @Column(name = "scrap_count")
    private Integer scrapCount = 0;

    @Builder.Default
    @Column(name = "profile_view_count")
    private Integer profileViewCount = 0;

    @Builder.Default
    @Column(name = "post_click_count")
    private Integer postClickCount = 0;

    @Builder.Default
    @Column(name = "active_days_last_30")
    private Integer activeDaysLast30 = 0;

    @Builder.Default
    @Column(name = "days_since_last_interaction")
    private Integer daysSinceLastInteraction = 0;

    @Builder.Default
    @Column(name = "interaction_score")
    private Double interactionScore = 0.0;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 기능 14: 추가
    @Builder.Default
    @Column(name = "linger_count")
    private Integer lingerCount = 0;

    @Builder.Default
    @Column(name = "mention_count")
    private Integer mentionCount = 0;

    @Builder.Default
    @Column(name = "total_linger_time_ms")
    private Long totalLingerTimeMs = 0L;

    @Builder.Default
    @Column(name = "days_since_first_interaction")
    private Integer daysSinceFirstInteraction = 0;

    // 기능 35: 명시적 상호작용만 기반 점수
    @Builder.Default
    @Column(name = "interaction_score_explicit")
    private Double interactionScoreExplicit = 0.0;
}
