package com.couponapi.domain.model;

import com.couponapi.domain.exception.CouponAlreadyDeletedException;
import com.couponapi.domain.model.valueobject.CouponCode;
import com.couponapi.domain.model.valueobject.DiscountValue;
import com.couponapi.domain.model.valueobject.ExpirationDate;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponTest {

    private static final Instant NOW = Instant.parse("2025-05-28T12:00:00Z");
    private static final Instant FUTURE = Instant.parse("2025-12-31T23:59:59Z");

    @Test
    void shouldCreateCouponAsActiveAndNotRedeemed() {
        Coupon coupon = createCoupon(false);

        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.ACTIVE);
        assertThat(coupon.isRedeemed()).isFalse();
        assertThat(coupon.isDeleted()).isFalse();
    }

    @Test
    void shouldCreateCouponAsPublishedWhenRequested() {
        Coupon coupon = createCoupon(true);

        assertThat(coupon.isPublished()).isTrue();
    }

    @Test
    void shouldSoftDeleteCoupon() {
        Coupon coupon = createCoupon(false);

        coupon.delete(NOW);

        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.DELETED);
        assertThat(coupon.getDeletedAt()).isEqualTo(NOW);
        assertThat(coupon.isDeleted()).isTrue();
    }

    @Test
    void shouldNotAllowDeletingAlreadyDeletedCoupon() {
        Coupon coupon = createCoupon(false);
        coupon.delete(NOW);

        assertThatThrownBy(() -> coupon.delete(NOW))
                .isInstanceOf(CouponAlreadyDeletedException.class);
    }

    @Test
    void shouldRejectBlankDescription() {
        assertThatThrownBy(() -> Coupon.create(
                CouponCode.fromRaw("ABC123"),
                "  ",
                DiscountValue.of(new BigDecimal("0.8")),
                ExpirationDate.forCreation(FUTURE, NOW),
                false
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectNullDescription() {
        assertThatThrownBy(() -> Coupon.create(
                CouponCode.fromRaw("ABC123"),
                null,
                DiscountValue.of(new BigDecimal("0.8")),
                ExpirationDate.forCreation(FUTURE, NOW),
                false
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRestoreCouponFromPersistence() {
        UUID id = UUID.randomUUID();
        CouponCode code = CouponCode.fromRaw("RST123");
        ExpirationDate expiration = ExpirationDate.restore(FUTURE);

        Coupon coupon = Coupon.restore(
                id,
                code,
                "Restored",
                DiscountValue.of(new BigDecimal("1.0")),
                expiration,
                CouponStatus.DELETED,
                true,
                true,
                NOW
        );

        assertThat(coupon.getId()).isEqualTo(id);
        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.DELETED);
        assertThat(coupon.isPublished()).isTrue();
        assertThat(coupon.isRedeemed()).isTrue();
        assertThat(coupon.getDeletedAt()).isEqualTo(NOW);
    }

    @Test
    void shouldConsiderCouponsEqualWhenSameId() {
        UUID id = UUID.randomUUID();
        Coupon first = Coupon.restore(
                id,
                CouponCode.fromRaw("EQ1234"),
                "A",
                DiscountValue.of(new BigDecimal("0.8")),
                ExpirationDate.restore(FUTURE),
                CouponStatus.ACTIVE,
                false,
                false,
                null
        );
        Coupon second = Coupon.restore(
                id,
                CouponCode.fromRaw("EQ5678"),
                "B",
                DiscountValue.of(new BigDecimal("1.0")),
                ExpirationDate.restore(FUTURE),
                CouponStatus.INACTIVE,
                true,
                true,
                NOW
        );

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    void shouldConsiderCouponsDifferentWhenIdsDiffer() {
        Coupon first = createCoupon(false);
        Coupon second = createCoupon(false);

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldReturnInactiveWhenExpirationHasPassed() {
        Coupon coupon = createCoupon(false);

        Instant afterExpiration = FUTURE.plusSeconds(1);

        assertThat(coupon.effectiveStatus(afterExpiration)).isEqualTo(CouponStatus.INACTIVE);
    }

    @Test
    void shouldKeepDeletedStatusRegardlessOfExpiration() {
        Coupon coupon = createCoupon(false);
        coupon.delete(NOW);

        assertThat(coupon.effectiveStatus(FUTURE.plusSeconds(1))).isEqualTo(CouponStatus.DELETED);
    }

    private Coupon createCoupon(boolean published) {
        return Coupon.create(
                CouponCode.fromRaw("ABC123"),
                "Valid description",
                DiscountValue.of(new BigDecimal("0.8")),
                ExpirationDate.forCreation(FUTURE, NOW),
                published
        );
    }
}
