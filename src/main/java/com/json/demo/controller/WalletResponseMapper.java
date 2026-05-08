package com.json.demo.controller;

import com.json.demo.model.UserWallet;
import com.json.demo.web.dto.UserWalletResponse;
import org.springframework.stereotype.Component;

@Component
public class WalletResponseMapper {
    public UserWalletResponse toResponse(UserWallet wallet) {
        return new UserWalletResponse(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getBalance(),
                wallet.getCreatedAt(),
                wallet.getUpdatedAt()
        );
    }
}
