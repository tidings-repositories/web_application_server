package com.delivalue.tidings.domain.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 기능 10/11: ML 안전 모델 임계값 설정 — VisibilityLib ThresholdConfig (DOC_08/28)
 */
@Entity
@Table(name = "safety_model_threshold")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SafetyModelThreshold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 모델 식별자 (toxicity / nsfwMedia / nsfwText / spam / pBlock / abuse_*)
    @Column(name = "model_name", length = 50, nullable = false, unique = true)
    private String modelName;

    // DROP 임계값 (초과 시 콘텐츠 숨김)
    @Column(name = "drop_threshold")
    private Double dropThreshold;

    // INTERSTITIAL 임계값 (경고 표시)
    @Column(name = "interstitial_threshold")
    private Double interstitialThreshold;

    // DOWNRANK 임계값 (랭킹 하향)
    @Column(name = "downrank_threshold")
    private Double downrankThreshold;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
