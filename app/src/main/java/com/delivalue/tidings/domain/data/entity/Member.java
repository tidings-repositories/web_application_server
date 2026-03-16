package com.delivalue.tidings.domain.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {

    @Id
    private String id;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "badge_id")
    private Badge badge;

    @Column(name = "public_id")
    private String publicId;

    private String name;

    private String bio;

    @Column(name = "profileImage")
    private String profileImage;

    private String email;

    @Column(name = "following_count")
    private int followingCount;

    @Column(name = "follower_count")
    private int followerCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // ⚠️ 주의1 반영: bannedAt → suspendedAt 컬럼명 변경 (DB 마이그레이션 필요)
    @Column(name = "suspended_at")
    private LocalDateTime suspendedAt;

    @Column(name = "ban_reason")
    private String banReason;

    // ⚠️ 주의1 반영: userState ENUM 추가
    @Column(name = "user_state", length = 20)
    @Builder.Default
    private String userState = "NORMAL"; // NORMAL / SUSPENDED / DEACTIVATED / RESTRICTED / OFFBOARDED

    @Column(name = "deactivated_at")
    private LocalDateTime deactivatedAt;

    // 기능 4: TweepCred (DOC_07)
    @Column(name = "reputation_score")
    @Builder.Default
    private Integer reputationScore = 50;

    @Column(name = "reputation_updated_at")
    private LocalDateTime reputationUpdatedAt;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;

    @Column(name = "account_age_category", length = 20)
    private String accountAgeCategory; // NEW / ACTIVE / RETURNING

    // 기능 11: PushService HealthFeatureGetter (DOC_29/30)
    @Column(name = "nudity_rate")
    @Builder.Default
    private Double nudityRate = 0.0;

    @Column(name = "author_report_rate")
    @Builder.Default
    private Double authorReportRate = 0.0;

    @Column(name = "author_dislike_rate")
    @Builder.Default
    private Double authorDislikeRate = 0.0;

    @Column(name = "has_nsfw_token")
    @Builder.Default
    private Boolean hasNsfwToken = false;

    @Column(name = "abuse_strike_level", length = 30)
    private String abuseStrikeLevel; // TOP_2_PERCENT 등

    @Column(name = "quality_tier_updated_at")
    private LocalDateTime qualityTierUpdatedAt;

    // 기능 12: PushService FatiguePredicate (DOC_29)
    @Column(name = "last_notified_at")
    private LocalDateTime lastNotifiedAt;

    @Column(name = "push_opt_out_score")
    @Builder.Default
    private Double pushOptOutScore = 0.0;

    // 기능 19: FRS 지역 필터 (DOC_17)
    @Column(name = "country_code", length = 10)
    private String countryCode;

    @Column(name = "language_code", length = 10)
    private String languageCode;

    // 기능 19: TweepCred PageRank 가중치 (DOC_07)
    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;

    // 기능 19: Trust & Safety (DOC_08)
    @Column(name = "user_toxicity_score")
    private Double userToxicityScore;

    // 기능 19: PushService FatiguePredicate 선행 체크 (DOC_29)
    @Column(name = "push_optin_status", length = 20)
    @Builder.Default
    private String pushOptinStatus = "ALL"; // ALL / SOCIAL_ONLY / NONE
}
