package com.couponapi.web.dto;

import com.couponapi.application.dto.CouponOutput;
import com.couponapi.domain.model.CouponStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CouponResponse(
        UUID id,
        String code,
        String description,
        BigDecimal discountValue,
        Instant expirationDate,
        CouponStatus status,
        boolean published,
        boolean redeemed
) {

    public static CouponResponse from(CouponOutput output) {
        return new CouponResponse(
                output.id(),
                output.code(),
                output.description(),
                output.discountValue(),
                output.expirationDate(),
                output.status(),
                output.published(),
                output.redeemed()
        );
    }
}
