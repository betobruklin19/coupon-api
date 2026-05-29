package com.couponapi.domain.exception;

public class CouponAlreadyDeletedException extends DomainException {

    public CouponAlreadyDeletedException() {
        super("Coupon is already deleted");
    }
}
