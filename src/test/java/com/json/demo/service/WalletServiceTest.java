package com.json.demo.service;

import com.json.demo.model.UserWallet;
import com.json.demo.repository.WalletRepository;
import com.json.demo.web.exception.InsufficientBalanceException;
import com.json.demo.web.exception.WalletNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

    @InjectMocks
    private WalletService walletService;

    private UserWallet wallet;

    @BeforeEach
    void setUp() {
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
        when(walletRepository.findByUserId("user-1")).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        UserWallet result = walletService.topUp("user-1", new BigDecimal("50.00"));
        assertThat(result.getBalance()).isEqualByComparingTo("150.00");
    }

    @Test
    void withdraw_success() {
        when(walletRepository.findByUserId("user-1")).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        UserWallet result = walletService.withdraw("user-1", new BigDecimal("30.00"));
        assertThat(result.getBalance()).isEqualByComparingTo("70.00");
    }

    @Test
    void withdraw_insufficientBalance() {
        when(walletRepository.findByUserId("user-1")).thenReturn(Optional.of(wallet));
        assertThatThrownBy(() -> walletService.withdraw("user-1", new BigDecimal("200.00")))
                .isInstanceOf(InsufficientBalanceException.class);
    }
}
