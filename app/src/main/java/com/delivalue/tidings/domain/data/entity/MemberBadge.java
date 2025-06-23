package com.delivalue.tidings.domain.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberBadge {
    @Id
    private int id;

    @Column(name = "member_id")
    private String memberId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "badge_id")
    private Badge badge;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
