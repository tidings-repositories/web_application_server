package com.delivalue.tidings.domain.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 기능 32: 게이머 명시적 게임 선호 프로필 — 콜드스타트 해결 (게이밍 SNS 특화)
 */
@Entity
@Table(name = "member_game_profile")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberGameProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private String memberId;

    // Topic.id 참조 (category=GAME_TITLE)
    @Column(name = "topic_id", nullable = false)
    private Long topicId;

    // MAIN: 주 플레이 게임, SUB: 서브 게임, PAST: 과거 플레이
    @Column(name = "play_type", length = 10)
    @Builder.Default
    private String playType = "MAIN";

    // 자가 평가 실력 (BEGINNER / INTERMEDIATE / ADVANCED / EXPERT)
    @Column(name = "skill_level", length = 20)
    private String skillLevel;

    // 주로 사용하는 플랫폼 코드 (PC / MOBILE / PS5 등)
    @Column(name = "platform", length = 20)
    private String platform;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
