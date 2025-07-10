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
public class CouponLogId implements Serializable {

    @Column(name = "member_id")
    private String memberId;

    @Column(name = "coupon_id")
    private String couponId;
}
