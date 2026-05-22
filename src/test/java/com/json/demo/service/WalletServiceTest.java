package com.json.demo.service;

import com.json.demo.model.UserWallet;
import com.json.demo.repository.WalletRepository;
import com.json.demo.service.transaction.PaymentMethod;
import com.json.demo.service.transaction.TopUpTransactionStrategy;
import com.json.demo.service.transaction.WalletTransactionStrategyFactory;
import com.json.demo.service.transaction.WithdrawTransactionStrategy;
import com.json.demo.web.exception.InsufficientBalanceException;
import com.json.demo.web.exception.InvalidAmountException;
import com.json.demo.web.exception.WalletNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    private WalletService walletService;

    private UserWallet wallet;

    @BeforeEach
    void setUp() {
        WalletTransactionStrategyFactory strategyFactory = new WalletTransactionStrategyFactory(
                java.util.List.of(new TopUpTransactionStrategy(), new WithdrawTransactionStrategy())
        );
        walletService = new WalletService(walletRepository, strategyFactory);

        wallet = UserWallet.builder()
                .id(UUID.randomUUID())
                .userId("user-1")
                .balance(new BigDecimal("100.00"))
                .build();
    }

    @Test
    void getWalletByUserId_found() {
        when(walletRepository.findByUserId("user-1")).thenReturn(Optional.of(wallet));
        UserWallet result = walletService.getWalletByUserId("user-1");
        assertThat(result.getUserId()).isEqualTo("user-1");
        assertThat(result.getBalance()).isEqualByComparingTo("100.00");
    }

    @Test
    void getWalletByUserId_notFound() {
        when(walletRepository.findByUserId("unknown")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> walletService.getWalletByUserId("unknown"))
                .isInstanceOf(WalletNotFoundException.class);
    }

    @Test
    void createWallet_newUser() {
        when(walletRepository.findByUserId("user-2")).thenReturn(Optional.empty());
        when(walletRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        UserWallet result = walletService.createWallet("user-2", new BigDecimal("50.00"));
        assertThat(result.getUserId()).isEqualTo("user-2");
        assertThat(result.getBalance()).isEqualByComparingTo("50.00");
    }

@Test
void topUp_success() {
    when(walletRepository.findByUserIdForUpdate("user-1")).thenReturn(Optional.of(wallet));
    when(walletRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    UserWallet result = walletService.topUp("user-1", new BigDecimal("50.00"), PaymentMethod.BANK_TRANSFER); // tambahin
    assertThat(result.getBalance()).isEqualByComparingTo("150.00");
}

    @Test
    void withdraw_success() {
        when(walletRepository.findByUserIdForUpdate("user-1")).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        UserWallet result = walletService.withdraw("user-1", new BigDecimal("30.00"));
        assertThat(result.getBalance()).isEqualByComparingTo("70.00");
    }

    @Test
    void withdraw_insufficientBalance() {
        when(walletRepository.findByUserIdForUpdate("user-1")).thenReturn(Optional.of(wallet));
        assertThatThrownBy(() -> walletService.withdraw("user-1", new BigDecimal("200.00")))
                .isInstanceOf(InsufficientBalanceException.class);
    }

    @Test
    void createWallet_userAlreadyExists_returnsExistingWallet() {
        when(walletRepository.findByUserId("user-1")).thenReturn(Optional.of(wallet));

        UserWallet result = walletService.createWallet("user-1", new BigDecimal("50.00"));

        assertThat(result.getUserId()).isEqualTo("user-1");
        assertThat(result.getBalance()).isEqualByComparingTo("100.00");
        verify(walletRepository, never()).save(any());
    }

 @Test
void topUp_newUser_createsWalletFromZero() {
    when(walletRepository.findByUserIdForUpdate("new-user")).thenReturn(Optional.empty());
    when(walletRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    UserWallet result = walletService.topUp("new-user", new BigDecimal("25.00"), PaymentMethod.BANK_TRANSFER); // tambahin
    assertThat(result.getUserId()).isEqualTo("new-user");
    assertThat(result.getBalance()).isEqualByComparingTo("25.00");
}

    @Test
    void withdraw_walletNotFound_throwsException() {
        when(walletRepository.findByUserIdForUpdate("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> walletService.withdraw("unknown", new BigDecimal("1.00")))
                .isInstanceOf(WalletNotFoundException.class);
    }

@Test
void topUp_invalidAmount_throwsException() {
    assertThatThrownBy(() -> walletService.topUp("user-1", BigDecimal.ZERO, PaymentMethod.BANK_TRANSFER)) // tambahin
            .isInstanceOf(InvalidAmountException.class);
}

    @Test
    void createWallet_negativeInitialBalance_throwsException() {
        assertThatThrownBy(() -> walletService.createWallet("user-1", new BigDecimal("-1.00")))
                .isInstanceOf(InvalidAmountException.class);
    }
}
