package com.couponapi.domain.model;

import com.couponapi.domain.exception.CouponAlreadyDeletedException;
import com.couponapi.domain.model.valueobject.CouponCode;
import com.couponapi.domain.model.valueobject.DiscountValue;
import com.couponapi.domain.model.valueobject.ExpirationDate;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Coupon {

    private final UUID id;
    private final CouponCode code;
    private final String description;
    private final DiscountValue discountValue;
    private final ExpirationDate expirationDate;
    private CouponStatus status;
    private final boolean published;
    private final boolean redeemed;
    private Instant deletedAt;

    private Coupon(
            UUID id,
            CouponCode code,
            String description,
            DiscountValue discountValue,
            ExpirationDate expirationDate,
            CouponStatus status,
            boolean published,
            boolean redeemed,
            Instant deletedAt
    ) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.discountValue = discountValue;
        this.expirationDate = expirationDate;
        this.status = status;
        this.published = published;
        this.redeemed = redeemed;
        this.deletedAt = deletedAt;
    }

    public static Coupon create(
            CouponCode code,
            String description,
            DiscountValue discountValue,
            ExpirationDate expirationDate,
            boolean published
    ) {
        validateDescription(description);

        return new Coupon(
                UUID.randomUUID(),
                code,
                description.trim(),
                discountValue,
                expirationDate,
                CouponStatus.ACTIVE,
                published,
                false,
                null
        );
    }

    public static Coupon restore(
            UUID id,
            CouponCode code,
            String description,
            DiscountValue discountValue,
            ExpirationDate expirationDate,
            CouponStatus status,
            boolean published,
            boolean redeemed,
            Instant deletedAt
    ) {
        return new Coupon(
                id,
                code,
                description,
                discountValue,
                expirationDate,
                status,
                published,
                redeemed,
                deletedAt
        );
    }

    public void delete(Instant referenceTime) {
        if (status == CouponStatus.DELETED) {
            throw new CouponAlreadyDeletedException();
        }

        this.status = CouponStatus.DELETED;
        this.deletedAt = referenceTime;
    }

    public boolean isDeleted() {
        return status == CouponStatus.DELETED;
    }

    public CouponStatus effectiveStatus(Instant referenceTime) {
        if (status == CouponStatus.DELETED) {
            return CouponStatus.DELETED;
        }
        if (!expirationDate.value().isAfter(referenceTime)) {
            return CouponStatus.INACTIVE;
        }
        return status;
    }

    private static void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description is required");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Coupon coupon = (Coupon) o;
        return Objects.equals(id, coupon.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
