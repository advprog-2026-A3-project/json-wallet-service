package com.json.demo.service;

import com.json.demo.model.UserWallet;

import java.math.BigDecimal;

public interface WalletOperations {
    UserWallet getWalletByUserId(String userId);

    UserWallet createWallet(String userId, BigDecimal initialBalance);

    UserWallet topUp(String userId, BigDecimal amount);

    UserWallet withdraw(String userId, BigDecimal amount);
}
