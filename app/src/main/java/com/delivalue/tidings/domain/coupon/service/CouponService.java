package com.delivalue.tidings.domain.coupon.service;

import com.delivalue.tidings.domain.data.entity.Badge;
import com.delivalue.tidings.domain.data.entity.Coupon;
import com.delivalue.tidings.domain.data.entity.CouponLog;
import com.delivalue.tidings.domain.data.entity.MemberBadge;
import com.delivalue.tidings.domain.data.entity.embed.CouponLogId;
import com.delivalue.tidings.domain.data.repository.BadgeRepository;
import com.delivalue.tidings.domain.data.repository.CouponLogRepository;
import com.delivalue.tidings.domain.data.repository.CouponRepository;
import com.delivalue.tidings.domain.data.repository.MemberBadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final CouponLogRepository logRepository;
    private final BadgeRepository badgeRepository;
    private final MemberBadgeRepository memberBadgeRepository;

    @Transactional
    private void useBadgeCoupon(String internalId, Coupon coupon, CouponLogId logId) {
        Integer badgeId = Integer.parseInt(coupon.getReward());
        Optional<Badge> findBadge = this.badgeRepository.findById(badgeId);
        if(findBadge.isEmpty()) throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        MemberBadge memberBadge = MemberBadge.builder()
                .memberId(internalId)
                .badge(findBadge.get())
                .createdAt(now).build();
        this.memberBadgeRepository.save(memberBadge);

        this.couponRepository.incrementUseCount(coupon.getId());

        CouponLog couponLog = new CouponLog(logId, now);
        this.logRepository.save(couponLog);
    }

    public void useCoupon(String internalId, String couponNumber) {
        Optional<Coupon> findCoupon = this.couponRepository.findById(couponNumber);
        if(findCoupon.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        Coupon coupon = findCoupon.get();

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        if(coupon.getIssuedAt().isAfter(now) || coupon.getExpiredAt().isBefore(now)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        CouponLogId logId = new CouponLogId(internalId, couponNumber);
        Optional<CouponLog> findLog = this.logRepository.findById(logId);
        if(findLog.isPresent()) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        switch(coupon.getType()) {
            case "badge":
                useBadgeCoupon(internalId, coupon, logId);
                break;
            default:
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
