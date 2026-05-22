package com.json.demo.service;

import com.json.demo.model.UserWallet;
import com.json.demo.repository.WalletRepository;
import com.json.demo.service.transaction.PaymentMethod;
import com.json.demo.service.transaction.WalletTransactionStrategyFactory;
import com.json.demo.service.transaction.WalletTransactionType;
import com.json.demo.web.exception.InvalidAmountException;
import com.json.demo.web.exception.WalletNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService implements WalletOperations {

    private final WalletRepository walletRepository;
    private final WalletTransactionStrategyFactory strategyFactory;

    @Override
    @Transactional(readOnly = true)
    public UserWallet getWalletByUserId(String userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for userId: " + userId));
    }

    @Override
    @Transactional
    public UserWallet createWallet(String userId, BigDecimal initialBalance) {
        BigDecimal safeInitialBalance = initialBalance == null ? BigDecimal.ZERO : initialBalance;
        validateAmountNotNegative(safeInitialBalance);

        return walletRepository.findByUserId(userId)
                .orElseGet(() -> walletRepository.save(
                        UserWallet.builder()
                                .userId(userId)
                                .balance(safeInitialBalance)
                                .build()));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserWallet topUp(String userId, BigDecimal amount, PaymentMethod paymentMethod) {
        validateAmountPositive(amount);

        UserWallet wallet = walletRepository.findByUserIdForUpdate(userId)
                .orElseGet(() -> UserWallet.builder()
                        .userId(userId)
                        .balance(BigDecimal.ZERO)
                        .build());
        UserWallet updatedWallet = strategyFactory.get(WalletTransactionType.TOP_UP).apply(wallet, amount);
        return walletRepository.save(updatedWallet);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserWallet withdraw(String userId, BigDecimal amount) {
        validateAmountPositive(amount);

        UserWallet wallet = walletRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for userId: " + userId));

        UserWallet updatedWallet = strategyFactory.get(WalletTransactionType.WITHDRAW).apply(wallet, amount);
        return walletRepository.save(updatedWallet);
    }

    private void validateAmountPositive(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }
    }

    private void validateAmountNotNegative(BigDecimal amount) {
        if (amount == null || amount.signum() < 0) {
            throw new InvalidAmountException("Initial balance must not be negative");
        }
    }
}