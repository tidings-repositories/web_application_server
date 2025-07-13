package com.delivalue.tidings.domain.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {
    @Id
    private String id;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "badge_id")
    private Badge badge;

    @Column(name = "public_id")
    private String publicId;

    private String name;

    private String bio;

    @Column(name = "profileImage")
    private String profileImage;

    private String email;

    @Column(name = "following_count")
    private int followingCount;

    @Column(name = "follower_count")
    private int followerCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "banned_at")
    private LocalDateTime bannedAt;

    @Column(name = "ban_reason")
    private String banReason;
}
