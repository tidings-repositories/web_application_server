package com.delivalue.tidings.domain.data.entity.embed;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * MemberBlock, MemberMute, MemberRetweetMute 등 회원 관계 복합 PK
 */
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class MemberRelationId implements Serializable {

    @Column(name = "blocker_id")
    private String blockerId;

    @Column(name = "target_id")
    private String targetId;
}
