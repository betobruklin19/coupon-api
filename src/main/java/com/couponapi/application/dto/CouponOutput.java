package com.couponapi.application.dto;

import com.couponapi.domain.model.Coupon;
import com.couponapi.domain.model.CouponStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CouponOutput(
        UUID id,
        String code,
        String description,
        BigDecimal discountValue,
        Instant expirationDate,
        CouponStatus status,
        boolean published,
        boolean redeemed
) {

    public static CouponOutput from(Coupon coupon, Instant referenceTime) {
        return new CouponOutput(
                coupon.getId(),
                coupon.getCode().value(),
                coupon.getDescription(),
                coupon.getDiscountValue().value(),
                coupon.getExpirationDate().value(),
                coupon.effectiveStatus(referenceTime),
                coupon.isPublished(),
                coupon.isRedeemed()
        );
    }
}
