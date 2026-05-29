package com.couponapi.domain.model.valueobject;

import com.couponapi.domain.exception.InvalidCouponCodeException;

import java.util.Objects;

public final class CouponCode {

    private static final int REQUIRED_LENGTH = 6;

    private final String value;

    private CouponCode(String value) {
        this.value = value;
    }

    public static CouponCode fromRaw(String rawCode) {
        if (rawCode == null || rawCode.isBlank()) {
            throw new InvalidCouponCodeException("Coupon code is required");
        }

        String sanitized = rawCode.replaceAll("[^a-zA-Z0-9]", "");

        if (sanitized.length() != REQUIRED_LENGTH) {
            throw new InvalidCouponCodeException(
                    "Coupon code must contain exactly 6 alphanumeric characters after sanitization");
        }

        return new CouponCode(sanitized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CouponCode that = (CouponCode) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
