package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.CouponLog;
import com.delivalue.tidings.domain.data.entity.embed.CouponLogId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponLogRepository extends JpaRepository<CouponLog, CouponLogId> {
}
