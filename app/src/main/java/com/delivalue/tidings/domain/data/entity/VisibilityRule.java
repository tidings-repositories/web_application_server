package com.delivalue.tidings.domain.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 기능 42: 가시성 규칙 — 조건+행동 선언적 정의 — VisibilityLib Rule/Policy (DOC_28)
 */
@Entity
@Table(name = "visibility_rule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class VisibilityRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_name", length = 100, nullable = false, unique = true)
    private String ruleName;

    // 적용 대상 타입 (POST / COMMENT / MEMBER)
    @Column(name = "target_type", length = 20, nullable = false)
    private String targetType;

    // 적용 표면 (HOME_FEED / SEARCH / NOTIFICATION / ALL)
    @Column(name = "surface", length = 50)
    @Builder.Default
    private String surface = "ALL";

    // 조건 표현식 (JSON: {"field":"toxicityScore","op":"gt","value":0.8})
    @Column(name = "condition_expr", columnDefinition = "TEXT", nullable = false)
    private String conditionExpr;

    // 적용 행동 (ALLOW / DOWNRANK / INTERSTITIAL / DROP)
    @Column(name = "action", length = 20, nullable = false)
    private String action;

    // DOWNRANK 시 점수 배율 (0.0~1.0)
    @Column(name = "downrank_factor")
    private Double downrankFactor;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "priority")
    @Builder.Default
    private Integer priority = 100; // 낮을수록 먼저 적용

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
