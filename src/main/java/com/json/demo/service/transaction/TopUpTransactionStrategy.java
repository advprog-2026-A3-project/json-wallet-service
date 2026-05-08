package com.json.demo.service.transaction;

import com.json.demo.model.UserWallet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TopUpTransactionStrategy implements WalletTransactionStrategy {
    @Override
    public WalletTransactionType type() {
        return WalletTransactionType.TOP_UP;
    }

    @Override
    public UserWallet apply(UserWallet wallet, BigDecimal amount) {
        wallet.setBalance(wallet.getBalance().add(amount));
        return wallet;
    }
}
