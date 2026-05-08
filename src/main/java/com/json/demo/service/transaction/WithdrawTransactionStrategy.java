package com.json.demo.service.transaction;

import com.json.demo.model.UserWallet;
import com.json.demo.web.exception.InsufficientBalanceException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class WithdrawTransactionStrategy implements WalletTransactionStrategy {
    @Override
    public WalletTransactionType type() {
        return WalletTransactionType.WITHDRAW;
    }

    @Override
    public UserWallet apply(UserWallet wallet, BigDecimal amount) {
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance for withdrawal");
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        return wallet;
    }
}
