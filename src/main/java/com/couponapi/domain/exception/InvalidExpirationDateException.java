package com.couponapi.domain.exception;

public class InvalidExpirationDateException extends DomainException {

    public InvalidExpirationDateException(String message) {
        super(message);
    }
}
