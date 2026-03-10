package com.delivalue.tidings.domain.data.entity;

import com.delivalue.tidings.domain.data.entity.embed.MemberRelationId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 기능 5: 뮤트 관계 — VisibilityLib Mute Filter (DOC_28)
 */
@Entity
@Table(name = "member_mute")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberMute {

    @EmbeddedId
    private MemberRelationId id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
