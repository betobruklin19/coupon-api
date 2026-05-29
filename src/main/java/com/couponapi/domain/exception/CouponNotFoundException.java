package com.couponapi.domain.exception;

public class CouponNotFoundException extends DomainException {

    public CouponNotFoundException() {
        super("Coupon not found");
    }
}
