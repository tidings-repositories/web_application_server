package com.delivalue.tidings.domain.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 기능 33: 행동 기반 장르 관심도 집계 — Heavy Ranker 피처 (게이밍 SNS 특화)
 */
@Entity
@Table(name = "member_genre_interest",
    uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "genre_code"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberGenreInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private String memberId;

    // MOBA / FPS / RPG / STRATEGY / SPORTS / PUZZLE / SIMULATION 등 22종
    @Column(name = "genre_code", nullable = false, length = 30)
    private String genreCode;

    // 행동 기반 관심도 점수 (0.0~1.0, 지수 감쇠 적용)
    @Builder.Default
    @Column(name = "interest_score")
    private Double interestScore = 0.0;

    // 최근 30일 해당 장르 게시물 상호작용 횟수
    @Builder.Default
    @Column(name = "interaction_count_30d")
    private Integer interactionCount30d = 0;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
