package com.delivalue.tidings.domain.coupon.controller;

import com.delivalue.tidings.domain.coupon.dto.CouponUseRequest;
import com.delivalue.tidings.domain.coupon.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
public class CouponController {

	private final CouponService couponService;

	@PostMapping
	public ResponseEntity<?> requestUseCoupon(
			@AuthenticationPrincipal String userId,
			@Valid @RequestBody CouponUseRequest body
	) {
		this.couponService.useCoupon(userId, body.getCoupon());
		return ResponseEntity.ok().build();
	}
}
