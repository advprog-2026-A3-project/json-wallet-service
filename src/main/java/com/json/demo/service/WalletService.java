package com.json.demo.service;

import com.json.demo.model.UserWallet;
import com.json.demo.repository.WalletRepository;
import com.json.demo.web.exception.InsufficientBalanceException;
import com.json.demo.web.exception.WalletNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    @Transactional(readOnly = true)
    public UserWallet getWalletByUserId(String userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for userId: " + userId));
    }

    @Transactional
    public UserWallet createWallet(String userId, BigDecimal initialBalance) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> walletRepository.save(
                        UserWallet.builder()
                                .userId(userId)
                                .balance(initialBalance != null ? initialBalance : BigDecimal.ZERO)
                                .build()));
    }

    @Transactional
    public UserWallet topUp(String userId, BigDecimal amount) {
        UserWallet wallet = walletRepository.findByUserId(userId)
                .orElseGet(() -> UserWallet.builder()
                        .userId(userId)
                        .balance(BigDecimal.ZERO)
                        .build());
        wallet.setBalance(wallet.getBalance().add(amount));
        return walletRepository.save(wallet);
    }

    @Transactional
    public UserWallet withdraw(String userId, BigDecimal amount) {
        UserWallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for userId: " + userId));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance for withdrawal");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        return walletRepository.save(wallet);
    }
}