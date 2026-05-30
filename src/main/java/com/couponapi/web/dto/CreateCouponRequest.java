package com.couponapi.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateCouponRequest(
        @NotBlank
        @Schema(example = "ABC-123")
        String code,

        @NotBlank
        @Schema(example = "Iure saepe amet. Excepturi saepe inventore nam doloremque vol")
        String description,

        @NotNull
        @Schema(example = "0.8")
        BigDecimal discountValue,

        @NotNull
        @Schema(example = "2025-11-04T17:14:45.180Z")
        Instant expirationDate,

        @Schema(example = "false", defaultValue = "false")
        Boolean published
) {

    public boolean publishedOrDefault() {
        return published != null && published;
    }
}
