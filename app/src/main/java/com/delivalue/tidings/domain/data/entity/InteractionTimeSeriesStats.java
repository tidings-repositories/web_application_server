package com.delivalue.tidings.domain.data.entity;

import com.delivalue.tidings.domain.data.entity.embed.InteractionTimeSeriesId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 기능 35: RealGraph 16타입 × 7통계 시계열 (Welford 알고리즘) — RealGraph DataRecordFeatures (DOC_06/24)
 *
 * 16가지 interactionType:
 *   like, unlike, comment, repost, scrap, unscrap, profileView, postClick,
 *   linger, mention, follow, unfollow, share, ntabClick, notificationOpen, videoWatch
 *
 * 7가지 통계 (Welford 온라인 알고리즘):
 *   count, mean, variance, countRecent, countDecayed, lastEventAt, daysSinceLast
 */
@Entity
@Table(name = "interaction_time_series_stats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class InteractionTimeSeriesStats {

    @EmbeddedId
    private InteractionTimeSeriesId id;

    @Builder.Default
    @Column(name = "total_count")
    private Long totalCount = 0L;

    @Column(name = "mean_interval_hours")
    private Double meanIntervalHours;

    @Column(name = "variance_interval_hours")
    private Double varianceIntervalHours;

    // 최근 7일 카운트
    @Builder.Default
    @Column(name = "count_recent_7d")
    private Integer countRecent7d = 0;

    // 지수 감쇠 적용 카운트 (반감기 24h)
    @Builder.Default
    @Column(name = "count_decayed")
    private Double countDecayed = 0.0;

    @Column(name = "last_event_at")
    private LocalDateTime lastEventAt;

    @Column(name = "days_since_last")
    private Integer daysSinceLast;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
