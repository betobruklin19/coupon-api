package com.couponapi.domain.model.valueobject;

import com.couponapi.domain.exception.InvalidExpirationDateException;

import java.time.Instant;
import java.util.Objects;

public final class ExpirationDate {

    private final Instant value;

    private ExpirationDate(Instant value) {
        this.value = value;
    }

    public static ExpirationDate forCreation(Instant expirationDate, Instant referenceTime) {
        if (expirationDate == null) {
            throw new InvalidExpirationDateException("Expiration date is required");
        }

        if (!expirationDate.isAfter(referenceTime)) {
            throw new InvalidExpirationDateException("Expiration date cannot be in the past");
        }

        return new ExpirationDate(expirationDate);
    }

    public static ExpirationDate restore(Instant expirationDate) {
        if (expirationDate == null) {
            throw new InvalidExpirationDateException("Expiration date is required");
        }
        return new ExpirationDate(expirationDate);
    }

    public Instant value() {
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
        ExpirationDate that = (ExpirationDate) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
