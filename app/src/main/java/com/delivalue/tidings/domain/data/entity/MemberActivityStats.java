package com.delivalue.tidings.domain.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 기능 4: 사용자 활동 집계 — TweepCred, UserEngagementAggregates (DOC_07/23)
 */
@Entity
@Table(name = "member_activity_stats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberActivityStats {

    @Id
    @Column(name = "member_id")
    private String memberId;

    // 팔로워 품질 집계
    @Builder.Default
    @Column(name = "follower_reputation_sum")
    private Double followerReputationSum = 0.0;

    @Builder.Default
    @Column(name = "follower_active_ratio")
    private Double followerActiveRatio = 0.0;

    // 최근 30일 참여 집계
    @Builder.Default
    @Column(name = "post_count_30d")
    private Integer postCount30d = 0;

    @Builder.Default
    @Column(name = "like_count_30d")
    private Integer likeCount30d = 0;

    @Builder.Default
    @Column(name = "comment_count_30d")
    private Integer commentCount30d = 0;

    @Builder.Default
    @Column(name = "repost_count_30d")
    private Integer repostCount30d = 0;

    @Builder.Default
    @Column(name = "received_like_count_30d")
    private Integer receivedLikeCount30d = 0;

    @Builder.Default
    @Column(name = "received_comment_count_30d")
    private Integer receivedCommentCount30d = 0;

    @Builder.Default
    @Column(name = "received_repost_count_30d")
    private Integer receivedRepostCount30d = 0;

    // 전체 누적
    @Builder.Default
    @Column(name = "total_post_count")
    private Integer totalPostCount = 0;

    @Builder.Default
    @Column(name = "total_received_like_count")
    private Long totalReceivedLikeCount = 0L;

    // 배치 갱신 시각
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
