package com.delivalue.tidings.domain.data.entity;

import com.delivalue.tidings.domain.data.entity.embed.MemberRelationId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 기능 26: 리포스트만 뮤트 (원본 포스트 유지) — VisibilityLib Retweet Mute (DOC_25/28)
 */
@Entity
@Table(name = "member_retweet_mute")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberRetweetMute {

    @EmbeddedId
    private MemberRelationId id; // blockerId=뮤터, targetId=리포스트 뮤트 대상

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
