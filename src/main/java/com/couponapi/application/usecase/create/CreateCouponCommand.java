package com.couponapi.application.usecase.create;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateCouponCommand(
        String code,
        String description,
        BigDecimal discountValue,
        Instant expirationDate,
        boolean published
) {
}
