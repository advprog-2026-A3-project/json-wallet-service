package com.json.demo.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record WithdrawRequest(
        @NotBlank
        String userId,

        @Positive
        BigDecimal amount
) {
}


