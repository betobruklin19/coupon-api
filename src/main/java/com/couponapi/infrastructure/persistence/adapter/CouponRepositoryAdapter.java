package com.couponapi.infrastructure.persistence.adapter;

import com.couponapi.domain.model.Coupon;
import com.couponapi.domain.repository.CouponRepository;
import com.couponapi.infrastructure.persistence.mapper.CouponPersistenceMapper;
import com.couponapi.infrastructure.persistence.repository.CouponJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CouponRepositoryAdapter implements CouponRepository {

    private final CouponJpaRepository jpaRepository;
    private final CouponPersistenceMapper mapper;

    @Override
    public Coupon save(Coupon coupon) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(coupon)));
    }

    @Override
    public Optional<Coupon> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
