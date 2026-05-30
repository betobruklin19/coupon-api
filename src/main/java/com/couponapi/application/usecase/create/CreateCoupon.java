package com.couponapi.application.usecase.create;

import com.couponapi.application.dto.CouponOutput;
import com.couponapi.domain.model.Coupon;
import com.couponapi.domain.model.valueobject.CouponCode;
import com.couponapi.domain.model.valueobject.DiscountValue;
import com.couponapi.domain.model.valueobject.ExpirationDate;
import com.couponapi.domain.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class CreateCoupon {

    private final CouponRepository couponRepository;
    private final Clock clock;

    public CouponOutput execute(CreateCouponCommand command) {
        Instant now = clock.instant();

        Coupon coupon = Coupon.create(
                CouponCode.fromRaw(command.code()),
                command.description(),
                DiscountValue.of(command.discountValue()),
                ExpirationDate.forCreation(command.expirationDate(), now),
                command.published()
        );

        Coupon savedCoupon = couponRepository.save(coupon);
        return CouponOutput.from(savedCoupon, now);
    }
}
