package com.couponapi.application.usecase.get;

import com.couponapi.application.dto.CouponOutput;
import com.couponapi.domain.exception.CouponNotFoundException;
import com.couponapi.domain.model.Coupon;
import com.couponapi.domain.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetCouponById {

    private final CouponRepository couponRepository;
    private final Clock clock;

    public CouponOutput execute(UUID id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(CouponNotFoundException::new);

        return CouponOutput.from(coupon, clock.instant());
    }
}
