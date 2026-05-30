package com.couponapi.domain.model.valueobject;

import com.couponapi.domain.exception.InvalidCouponCodeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponCodeTest {

    @Test
    void shouldSanitizeSpecialCharacters() {
        CouponCode code = CouponCode.fromRaw("ABC-123");

        assertThat(code.value()).isEqualTo("ABC123");
    }

    @Test
    void shouldAcceptAlphanumericCodeWithSixCharacters() {
        CouponCode code = CouponCode.fromRaw("ABC123");

        assertThat(code.value()).isEqualTo("ABC123");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "ABC12", "ABC1234", "ABC-12"})
    void shouldRejectInvalidCodes(String rawCode) {
        assertThatThrownBy(() -> CouponCode.fromRaw(rawCode))
                .isInstanceOf(InvalidCouponCodeException.class);
    }

    @Test
    void shouldRejectNullCode() {
        assertThatThrownBy(() -> CouponCode.fromRaw(null))
                .isInstanceOf(InvalidCouponCodeException.class);
    }

    @Test
    void shouldSupportEqualityByValue() {
        CouponCode first = CouponCode.fromRaw("ABC123");
        CouponCode second = CouponCode.fromRaw("ABC-123");
        CouponCode different = CouponCode.fromRaw("XYZ789");

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
        assertThat(first).isNotEqualTo(different);
    }
}
