package com.couponapi.domain.model.valueobject;

import com.couponapi.domain.exception.InvalidDiscountValueException;

import java.math.BigDecimal;
import java.util.Objects;

public final class DiscountValue {

    private static final BigDecimal MINIMUM = new BigDecimal("0.5");

    private final BigDecimal value;

    private DiscountValue(BigDecimal value) {
        this.value = value;
    }

    public static DiscountValue of(BigDecimal value) {
        if (value == null) {
            throw new InvalidDiscountValueException("Discount value is required");
        }

        if (value.compareTo(MINIMUM) < 0) {
            throw new InvalidDiscountValueException("Discount value must be at least 0.5");
        }

        return new DiscountValue(value);
    }

    public BigDecimal value() {
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
        DiscountValue that = (DiscountValue) o;
        return value.compareTo(that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value.stripTrailingZeros());
    }
}
