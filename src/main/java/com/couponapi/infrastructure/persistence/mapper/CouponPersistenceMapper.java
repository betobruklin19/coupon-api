package com.couponapi.infrastructure.persistence.mapper;

import com.couponapi.domain.model.Coupon;
import com.couponapi.domain.model.valueobject.CouponCode;
import com.couponapi.domain.model.valueobject.DiscountValue;
import com.couponapi.domain.model.valueobject.ExpirationDate;
import com.couponapi.infrastructure.persistence.entity.CouponJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class CouponPersistenceMapper {

    public CouponJpaEntity toEntity(Coupon coupon) {
        CouponJpaEntity entity = new CouponJpaEntity();
        entity.setId(coupon.getId());
        entity.setCode(coupon.getCode().value());
        entity.setDescription(coupon.getDescription());
        entity.setDiscountValue(coupon.getDiscountValue().value());
        entity.setExpirationDate(coupon.getExpirationDate().value());
        entity.setStatus(coupon.getStatus());
        entity.setPublished(coupon.isPublished());
        entity.setRedeemed(coupon.isRedeemed());
        entity.setDeletedAt(coupon.getDeletedAt());
        return entity;
    }

    public Coupon toDomain(CouponJpaEntity entity) {
        return Coupon.restore(
                entity.getId(),
                CouponCode.fromRaw(entity.getCode()),
                entity.getDescription(),
                DiscountValue.of(entity.getDiscountValue()),
                ExpirationDate.restore(entity.getExpirationDate()),
                entity.getStatus(),
                entity.isPublished(),
                entity.isRedeemed(),
                entity.getDeletedAt()
        );
    }
}
