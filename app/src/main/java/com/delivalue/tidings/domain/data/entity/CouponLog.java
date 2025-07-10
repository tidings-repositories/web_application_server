package com.delivalue.tidings.domain.data.entity;

import com.delivalue.tidings.domain.data.entity.embed.CouponLogId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CouponLog {

    @EmbeddedId
    private CouponLogId id;

    @Column(name = "used_at")
    private LocalDateTime usedAt;
}
