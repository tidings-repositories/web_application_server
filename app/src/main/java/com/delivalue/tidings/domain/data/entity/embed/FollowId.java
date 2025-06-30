package com.delivalue.tidings.domain.data.entity.embed;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class FollowId implements Serializable {

    @Column(name = "following_id")
    private String followingId;

    @Column(name = "follower_id")
    private String followerId;
}
