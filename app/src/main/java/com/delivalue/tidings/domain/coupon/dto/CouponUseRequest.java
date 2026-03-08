package com.delivalue.tidings.domain.coupon.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CouponUseRequest {
	@NotBlank
	private String coupon;
}
