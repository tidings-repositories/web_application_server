package com.delivalue.tidings.domain.data.entity;

import com.delivalue.tidings.domain.data.entity.embed.FollowId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Follow {

    @EmbeddedId
    private FollowId id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 기능 3: RealGraph 팔로잉 가중치 탐색 (DOC_06)
    @Column(name = "interaction_score")
    @Builder.Default
    private Double interactionScore = 0.0;

    @Column(name = "score_updated_at")
    private LocalDateTime scoreUpdatedAt;
}
