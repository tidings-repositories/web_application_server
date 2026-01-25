package com.delivalue.tidings.domain.coupon.controller;

import com.delivalue.tidings.domain.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
public class CouponController {

	private final CouponService couponService;

	@PostMapping
	public ResponseEntity<?> requestUseCoupon(
			@AuthenticationPrincipal String userId,
			@RequestBody Map<String, String> body
	) {
		String inputCoupon = body.get("coupon");

		if (inputCoupon == null) {
			return ResponseEntity.badRequest().build();
		}

		this.couponService.useCoupon(userId, inputCoupon);
		return ResponseEntity.ok().build();
	}
}
