package com.json.demo.service;

import com.json.demo.model.UserWallet;
import com.json.demo.service.transaction.PaymentMethod;

import java.math.BigDecimal;

public interface WalletOperations {
    UserWallet getWalletByUserId(String userId);

    UserWallet createWallet(String userId, BigDecimal initialBalance);

    UserWallet topUp(String userId, BigDecimal amount, PaymentMethod paymentMethod);

    UserWallet withdraw(String userId, BigDecimal amount);
}