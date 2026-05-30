package com.couponapi.web.controller;

import com.couponapi.application.usecase.create.CreateCoupon;
import com.couponapi.application.usecase.create.CreateCouponCommand;
import com.couponapi.application.usecase.delete.DeleteCoupon;
import com.couponapi.application.usecase.get.GetCouponById;
import com.couponapi.web.dto.CouponResponse;
import com.couponapi.web.dto.CreateCouponRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.couponapi.web.dto.ErrorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
@Tag(name = "Coupon", description = "Coupon API")
public class CouponController {

    private final CreateCoupon createCoupon;
    private final GetCouponById getCouponById;
    private final DeleteCoupon deleteCoupon;

    @PostMapping
    @Operation(summary = "Create a coupon")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Coupon created"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CouponResponse> create(@Valid @RequestBody CreateCouponRequest request) {
        var command = new CreateCouponCommand(
                request.code(),
                request.description(),
                request.discountValue(),
                request.expirationDate(),
                request.publishedOrDefault()
        );

        CouponResponse response = CouponResponse.from(createCoupon.execute(command));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a coupon by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Coupon found"),
            @ApiResponse(responseCode = "404", description = "Coupon not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public CouponResponse getById(@PathVariable UUID id) {
        return CouponResponse.from(getCouponById.execute(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a coupon")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Coupon deleted"),
            @ApiResponse(responseCode = "404", description = "Coupon not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Coupon already deleted",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteCoupon.execute(id);
        return ResponseEntity.noContent().build();
    }
}
