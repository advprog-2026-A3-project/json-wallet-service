package com.json.demo.controller;

import com.json.demo.model.UserWallet;
import com.json.demo.service.WalletService;
import com.json.demo.web.exception.WalletNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletViewController.class)
class WalletViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    private UserWallet dummyWallet() {
        return UserWallet.builder()
                .id(UUID.randomUUID())
                .userId("jenisa-001")
                .balance(new BigDecimal("100000.00"))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void dashboard_found_returns200() throws Exception {
        when(walletService.getWalletByUserId("jenisa-001")).thenReturn(dummyWallet());

        mockMvc.perform(get("/view/wallet/jenisa-001"))
                .andExpect(status().isOk())
                .andExpect(view().name("wallet"))
                .andExpect(model().attributeExists("wallet"));
    }

    @Test
    void dashboard_notFound_throws() throws Exception {
        when(walletService.getWalletByUserId("unknown"))
                .thenThrow(new WalletNotFoundException("Wallet not found"));

        mockMvc.perform(get("/view/wallet/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void topUp_success_returns200() throws Exception {
        when(walletService.topUp(eq("jenisa-001"), any())).thenReturn(dummyWallet());

        mockMvc.perform(post("/view/wallet/jenisa-001/topup")
                        .param("amount", "50000"))
                .andExpect(status().isOk())
                .andExpect(view().name("wallet"));
    }

    @Test
    void withdraw_success_returns200() throws Exception {
        when(walletService.withdraw(eq("jenisa-001"), any())).thenReturn(dummyWallet());

        mockMvc.perform(post("/view/wallet/jenisa-001/withdraw")
                        .param("amount", "20000"))
                .andExpect(status().isOk())
                .andExpect(view().name("wallet"));
    }
}