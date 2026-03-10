package com.delivalue.tidings.domain.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 기능 9: 토픽 마스터 (게임/장르/플랫폼 등) — SimClusters Community ID (DOC_04)
 * 기능 21: entityId, languageCode, isRecommendable, displayName 추가
 * 기능 31: CHARACTER, ESPORTS_TEAM, ESPORTS_PLAYER, TOURNAMENT, PATCH_NOTE 카테고리 추가
 */
@Entity
@Table(name = "topic")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 30)
    private String category; // GAME_TITLE / GENRE / PLATFORM / ESPORTS / GENERAL / CHARACTER / ESPORTS_TEAM / ESPORTS_PLAYER / TOURNAMENT / PATCH_NOTE

    @Column(name = "parent_topic_id")
    private Long parentTopicId;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 기능 21: FRS 외부 엔티티 참조 (Wikidata QID 등)
    @Column(name = "entity_id", length = 50)
    private String entityId;

    // 기능 21: TSPS 다국어 지원 (DOC_17)
    @Column(name = "language_code", length = 10)
    @Builder.Default
    private String languageCode = "ko";

    // 기능 21: FRS Topic Filter — 추천 후보 제외 필터 (DOC_17)
    @Column(name = "is_recommendable")
    @Builder.Default
    private Boolean isRecommendable = true;

    // 기능 21: UI 표시용 이름 (name은 내부 식별자)
    @Column(name = "display_name", length = 100)
    private String displayName;
}
