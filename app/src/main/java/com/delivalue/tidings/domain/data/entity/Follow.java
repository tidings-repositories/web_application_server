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
}
