package com.delivalue.tidings.domain.coupon.controller;

import com.delivalue.tidings.common.TokenProvider;
import com.delivalue.tidings.domain.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;
    private final TokenProvider tokenProvider;

    @PostMapping
    public ResponseEntity<?> requestUseCoupon(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Map<String, String> body) {
        int TOKEN_PREFIX_LENGTH = 7;

        String inputCoupon = body.get("coupon");

        if(inputCoupon == null) return ResponseEntity.badRequest().build();
        if(authorizationHeader != null
                && authorizationHeader.startsWith("Bearer ")
                && this.tokenProvider.validate(authorizationHeader.substring(TOKEN_PREFIX_LENGTH))) {
            String id = this.tokenProvider.getUserId(authorizationHeader.substring(TOKEN_PREFIX_LENGTH));

            this.couponService.useCoupon(id, inputCoupon);

            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
