package com.couponapi.domain.model.valueobject;

import com.couponapi.domain.exception.InvalidDiscountValueException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DiscountValueTest {

    @Test
    void shouldAcceptMinimumDiscountValue() {
        DiscountValue discountValue = DiscountValue.of(new BigDecimal("0.5"));

        assertThat(discountValue.value()).isEqualByComparingTo("0.5");
    }

    @Test
    void shouldAcceptValuesAboveMinimum() {
        DiscountValue discountValue = DiscountValue.of(new BigDecimal("1000"));

        assertThat(discountValue.value()).isEqualByComparingTo("1000");
    }

    @Test
    void shouldRejectValuesBelowMinimum() {
        assertThatThrownBy(() -> DiscountValue.of(new BigDecimal("0.49")))
                .isInstanceOf(InvalidDiscountValueException.class);
    }

    @Test
    void shouldRejectNullValue() {
        assertThatThrownBy(() -> DiscountValue.of(null))
                .isInstanceOf(InvalidDiscountValueException.class);
    }

    @Test
    void shouldSupportEqualityByValue() {
        DiscountValue first = DiscountValue.of(new BigDecimal("0.8"));
        DiscountValue second = DiscountValue.of(new BigDecimal("0.80"));
        DiscountValue different = DiscountValue.of(new BigDecimal("1.0"));

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
        assertThat(first).isNotEqualTo(different);
    }
}
