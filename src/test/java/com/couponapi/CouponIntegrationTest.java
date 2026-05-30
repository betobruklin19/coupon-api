package com.couponapi;

import com.couponapi.domain.model.CouponStatus;
import com.couponapi.infrastructure.persistence.entity.CouponJpaEntity;
import com.couponapi.infrastructure.persistence.repository.CouponJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CouponIntegrationTest {

    private static final Instant FIXED_NOW = Instant.parse("2025-05-28T12:00:00Z");
    private static final Instant FUTURE_EXPIRATION = Instant.parse("2025-12-31T23:59:59Z");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CouponJpaRepository couponJpaRepository;

    @BeforeEach
    void cleanDatabase() {
        couponJpaRepository.deleteAll();
    }

    @Test
    void shouldCreateCouponWithSanitizedCode() {
        Map<String, Object> request = Map.of(
                "code", "ABC-123",
                "description", "Integration test coupon",
                "discountValue", 0.8,
                "expirationDate", FUTURE_EXPIRATION.toString(),
                "published", false
        );

        ResponseEntity<Map> response = restTemplate.postForEntity("/coupon", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("code")).isEqualTo("ABC123");
        assertThat(response.getBody().get("status")).isEqualTo("ACTIVE");
        assertThat(response.getBody().get("published")).isEqualTo(false);
        assertThat(response.getBody().get("redeemed")).isEqualTo(false);

        UUID id = UUID.fromString(response.getBody().get("id").toString());
        CouponJpaEntity entity = couponJpaRepository.findById(id).orElseThrow();
        assertThat(entity.getCode()).isEqualTo("ABC123");
        assertThat(entity.getDeletedAt()).isNull();
    }

    @Test
    void shouldCreateCouponAsPublished() {
        Map<String, Object> request = Map.of(
                "code", "PUB123",
                "description", "Published coupon",
                "discountValue", 1.0,
                "expirationDate", FUTURE_EXPIRATION.toString(),
                "published", true
        );

        ResponseEntity<Map> response = restTemplate.postForEntity("/coupon", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().get("published")).isEqualTo(true);
    }

    @Test
    void shouldRejectDiscountBelowMinimum() {
        Map<String, Object> request = Map.of(
                "code", "LOW123",
                "description", "Invalid discount",
                "discountValue", 0.4,
                "expirationDate", FUTURE_EXPIRATION.toString()
        );

        ResponseEntity<Map> response = restTemplate.postForEntity("/coupon", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(couponJpaRepository.count()).isZero();
    }

    @Test
    void shouldRejectPastExpirationDate() {
        Map<String, Object> request = Map.of(
                "code", "PAST12",
                "description", "Past expiration",
                "discountValue", 0.8,
                "expirationDate", "2025-05-27T12:00:00Z"
        );

        ResponseEntity<Map> response = restTemplate.postForEntity("/coupon", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(couponJpaRepository.count()).isZero();
    }

    @Test
    void shouldGetCouponById() {
        UUID id = createCouponViaApi("GET123");

        ResponseEntity<Map> response = restTemplate.getForEntity("/coupon/" + id, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("id")).isEqualTo(id.toString());
        assertThat(response.getBody().get("code")).isEqualTo("GET123");
    }

    @Test
    void shouldReturnNotFoundWhenCouponDoesNotExist() {
        UUID randomId = UUID.randomUUID();

        ResponseEntity<Map> response = restTemplate.getForEntity("/coupon/" + randomId, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldSoftDeleteCouponAndKeepRecordInDatabase() {
        UUID id = createCouponViaApi("DEL123");

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/coupon/" + id,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        CouponJpaEntity entity = couponJpaRepository.findById(id).orElseThrow();
        assertThat(entity.getStatus()).isEqualTo(CouponStatus.DELETED);
        assertThat(entity.getDeletedAt()).isNotNull();

        ResponseEntity<Map> getResponse = restTemplate.getForEntity("/coupon/" + id, Map.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().get("status")).isEqualTo("DELETED");
    }

    @Test
    void shouldNotAllowDeletingAlreadyDeletedCoupon() {
        UUID id = createCouponViaApi("DEL456");

        restTemplate.exchange("/coupon/" + id, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

        ResponseEntity<Map> secondDelete = restTemplate.exchange(
                "/coupon/" + id,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Map.class
        );

        assertThat(secondDelete.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingUnknownCoupon() {
        ResponseEntity<Map> response = restTemplate.exchange(
                "/coupon/" + UUID.randomUUID(),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private UUID createCouponViaApi(String code) {
        Map<String, Object> request = Map.of(
                "code", code,
                "description", "Coupon for integration test",
                "discountValue", 0.8,
                "expirationDate", FUTURE_EXPIRATION.toString()
        );

        ResponseEntity<Map> response = restTemplate.postForEntity("/coupon", request, Map.class);
        return UUID.fromString(response.getBody().get("id").toString());
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        Clock clock() {
            return Clock.fixed(FIXED_NOW, ZoneOffset.UTC);
        }
    }
}
