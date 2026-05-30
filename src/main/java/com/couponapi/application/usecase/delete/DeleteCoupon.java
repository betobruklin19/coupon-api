package com.couponapi.application.usecase.delete;

import com.couponapi.domain.exception.CouponNotFoundException;
import com.couponapi.domain.model.Coupon;
import com.couponapi.domain.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeleteCoupon {

    private final CouponRepository couponRepository;
    private final Clock clock;

    public void execute(UUID id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(CouponNotFoundException::new);

        coupon.delete(clock.instant());
        couponRepository.save(coupon);
    }
}
