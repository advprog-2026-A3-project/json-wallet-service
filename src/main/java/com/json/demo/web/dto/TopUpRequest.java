package com.json.demo.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

import com.json.demo.service.transaction.PaymentMethod;

public record TopUpRequest(
        @NotBlank
        String userId,

        @Positive
        BigDecimal amount,

        @NotNull
        PaymentMethod paymentMethod
) {
}