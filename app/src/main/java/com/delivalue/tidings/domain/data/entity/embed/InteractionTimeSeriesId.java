package com.delivalue.tidings.domain.data.entity.embed;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * InteractionTimeSeriesStats 복합 PK: 사용자 쌍 × 상호작용 유형
 */
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class InteractionTimeSeriesId implements Serializable {

    @Column(name = "source_user_id")
    private String sourceUserId;

    @Column(name = "target_user_id")
    private String targetUserId;

    // 16가지 상호작용 유형 (like/comment/repost/scrap/profileView/postClick/linger/mention 등)
    @Column(name = "interaction_type", length = 30)
    private String interactionType;
}
