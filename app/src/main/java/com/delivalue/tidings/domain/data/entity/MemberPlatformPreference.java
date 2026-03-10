package com.delivalue.tidings.domain.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 기능 33: 행동 기반 기기 선호도 집계 — OON 포스트 필터 (게이밍 SNS 특화)
 */
@Entity
@Table(name = "member_platform_preference",
    uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "platform_code"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberPlatformPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private String memberId;

    // PC / MOBILE / PS5 / XBOX / SWITCH / VR / CONSOLE 등 7종
    @Column(name = "platform_code", nullable = false, length = 20)
    private String platformCode;

    // 행동 기반 선호도 점수 (0.0~1.0)
    @Builder.Default
    @Column(name = "preference_score")
    private Double preferenceScore = 0.0;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
