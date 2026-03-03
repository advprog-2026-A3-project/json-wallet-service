package com.json.demo.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TopUpRequest(
        @NotBlank
        String userId,

        @Positive
        BigDecimal amount
) {
}


