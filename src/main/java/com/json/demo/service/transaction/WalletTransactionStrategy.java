package com.json.demo.service.transaction;

import com.json.demo.model.UserWallet;

import java.math.BigDecimal;

public interface WalletTransactionStrategy {
    WalletTransactionType type();

    UserWallet apply(UserWallet wallet, BigDecimal amount);
}
