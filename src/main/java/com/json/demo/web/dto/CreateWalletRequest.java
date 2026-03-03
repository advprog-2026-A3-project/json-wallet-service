package com.json.demo.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateWalletRequest(
        @NotBlank
        String userId,

        @PositiveOrZero
        BigDecimal initialBalance
) {
}


