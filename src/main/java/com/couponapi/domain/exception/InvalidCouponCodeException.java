package com.couponapi.domain.exception;

public class InvalidCouponCodeException extends DomainException {

    public InvalidCouponCodeException(String message) {
        super(message);
    }
}
