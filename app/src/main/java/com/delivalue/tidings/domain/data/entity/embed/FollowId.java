package com.delivalue.tidings.domain.data.entity.embed;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;

@Embeddable
@Getter
public class FollowId implements Serializable {

    @Column(name = "following_id")
    private String followingId;

    @Column(name = "follower_id")
    private String followerId;
}
