package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, String> {

    @Query("UPDATE Coupon c SET c.useCount = c.useCount + 1 WHERE c.id = :id")
    int incrementUseCount(@Param("id") String id);
}
