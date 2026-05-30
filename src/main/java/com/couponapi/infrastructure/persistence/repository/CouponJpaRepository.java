package com.couponapi.infrastructure.persistence.repository;

import com.couponapi.infrastructure.persistence.entity.CouponJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CouponJpaRepository extends JpaRepository<CouponJpaEntity, UUID> {
}
