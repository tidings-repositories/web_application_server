package com.delivalue.tidings.domain.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 기능 42: 제품 표면별 안전 수준 정의 — VisibilityLib SafetyLevel (DOC_28)
 */
@Entity
@Table(name = "safety_level_config")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SafetyLevelConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 제품 표면 식별자 (HOME_FEED / OON_FEED / SEARCH / NOTIFICATION / PROFILE)
    @Column(name = "surface_name", length = 50, nullable = false, unique = true)
    private String surfaceName;

    // 해당 표면에 적용할 SafetyLevel 코드
    @Column(name = "safety_level", length = 50, nullable = false)
    private String safetyLevel;

    // 해당 표면에서 NSFW 필터링 적용 여부
    @Builder.Default
    @Column(name = "enable_nsfw_filter")
    private Boolean enableNsfwFilter = true;

    // 해당 표면에서 Toxicity 필터링 적용 여부
    @Builder.Default
    @Column(name = "enable_toxicity_filter")
    private Boolean enableToxicityFilter = true;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
