package com.json.demo.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record UserWalletResponse(
        UUID id,
        String userId,
        BigDecimal balance,
        Instant createdAt,
        Instant updatedAt
) {
}


