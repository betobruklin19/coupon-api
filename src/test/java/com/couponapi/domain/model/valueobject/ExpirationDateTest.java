package com.couponapi.domain.model.valueobject;

import com.couponapi.domain.exception.InvalidExpirationDateException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExpirationDateTest {

    @Test
    void shouldRejectPastExpirationDateOnCreation() {
        Instant now = Instant.parse("2025-05-28T12:00:00Z");
        Instant pastDate = Instant.parse("2025-05-27T12:00:00Z");

        assertThatThrownBy(() -> ExpirationDate.forCreation(pastDate, now))
                .isInstanceOf(InvalidExpirationDateException.class);
    }

    @Test
    void shouldRejectCurrentInstantAsExpirationDateOnCreation() {
        Instant now = Instant.parse("2025-05-28T12:00:00Z");

        assertThatThrownBy(() -> ExpirationDate.forCreation(now, now))
                .isInstanceOf(InvalidExpirationDateException.class);
    }

    @Test
    void shouldAcceptFutureExpirationDateOnCreation() {
        Instant now = Instant.parse("2025-05-28T12:00:00Z");
        Instant futureDate = Instant.parse("2025-12-31T23:59:59Z");

        ExpirationDate expirationDate = ExpirationDate.forCreation(futureDate, now);

        assertThat(expirationDate.value()).isEqualTo(futureDate);
    }

    @Test
    void shouldRestoreExpirationDate() {
        Instant futureDate = Instant.parse("2025-12-31T23:59:59Z");

        ExpirationDate expirationDate = ExpirationDate.restore(futureDate);

        assertThat(expirationDate.value()).isEqualTo(futureDate);
    }

    @Test
    void shouldRejectNullExpirationDateOnCreation() {
        Instant now = Instant.parse("2025-05-28T12:00:00Z");

        assertThatThrownBy(() -> ExpirationDate.forCreation(null, now))
                .isInstanceOf(InvalidExpirationDateException.class);
    }

    @Test
    void shouldRejectNullExpirationDateOnRestore() {
        assertThatThrownBy(() -> ExpirationDate.restore(null))
                .isInstanceOf(InvalidExpirationDateException.class);
    }

    @Test
    void shouldSupportEqualityByValue() {
        Instant futureDate = Instant.parse("2025-12-31T23:59:59Z");
        Instant now = Instant.parse("2025-05-28T12:00:00Z");

        ExpirationDate first = ExpirationDate.forCreation(futureDate, now);
        ExpirationDate second = ExpirationDate.restore(futureDate);
        ExpirationDate different = ExpirationDate.forCreation(
                Instant.parse("2026-01-01T00:00:00Z"),
                now
        );

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
        assertThat(first).isNotEqualTo(different);
    }
}
