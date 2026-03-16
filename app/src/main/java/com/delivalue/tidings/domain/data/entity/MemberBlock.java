package com.delivalue.tidings.domain.data.entity;

import com.delivalue.tidings.domain.data.entity.embed.MemberRelationId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 기능 5: 차단 관계 — VisibilityLib Block Filter (DOC_28)
 */
@Entity
@Table(name = "member_block")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberBlock {

    @EmbeddedId
    private MemberRelationId id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
