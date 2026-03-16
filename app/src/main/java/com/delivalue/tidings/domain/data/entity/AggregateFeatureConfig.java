package com.delivalue.tidings.domain.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 기능 38: 실시간 집계 설정 테이블 (그룹핑 키 × 메트릭 × 반감기) — Aggregation Framework (DOC_23)
 */
@Entity
@Table(name = "aggregate_feature_config")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AggregateFeatureConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 집계 피처 이름 (예: user.like.30m, post.view.24h)
    @Column(name = "feature_name", length = 100, nullable = false, unique = true)
    private String featureName;

    // 그룹핑 키 타입 (USER / POST / USER_POST_PAIR / TOPIC)
    @Column(name = "group_key_type", length = 30, nullable = false)
    private String groupKeyType;

    // 집계 메트릭 (COUNT / SUM / AVG / DECAY_SUM)
    @Column(name = "metric_type", length = 20, nullable = false)
    private String metricType;

    // 집계 대상 이벤트 타입 (POST_LIKE / POST_VIEW 등 30종 중 선택)
    @Column(name = "event_type", length = 50, nullable = false)
    private String eventType;

    // 반감기 (분 단위): 30 / 1440(24h) / 2880(48h) / 4320(72h)
    @Column(name = "half_life_minutes")
    private Integer halfLifeMinutes;

    // Redis TTL (초)
    @Column(name = "redis_ttl_seconds")
    private Integer redisTtlSeconds;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
