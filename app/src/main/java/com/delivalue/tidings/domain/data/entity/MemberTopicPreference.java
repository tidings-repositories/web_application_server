package com.delivalue.tidings.domain.data.entity;

import com.delivalue.tidings.domain.data.entity.embed.MemberTopicId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 기능 21: 토픽 수신 거부/일시중지 — FRS Topic Opt-Out, TSPS Paused (DOC_17)
 */
@Entity
@Table(name = "member_topic_preference")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberTopicPreference {

    @EmbeddedId
    private MemberTopicId id;

    // OPTED_OUT: 영구 수신 거부 / PAUSED: 일시중지 / FOLLOWING: 명시적 팔로우
    @Column(name = "preference_type", length = 20, nullable = false)
    private String preferenceType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "resumed_at")
    private LocalDateTime resumedAt; // PAUSED 해제 시각
}
