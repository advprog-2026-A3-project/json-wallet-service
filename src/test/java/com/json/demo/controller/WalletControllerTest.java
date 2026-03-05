package com.json.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.json.demo.model.UserWallet;
import com.json.demo.service.WalletService;
import com.json.demo.web.exception.WalletNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    void getWallet_found_returns200() throws Exception {
        when(walletService.getWalletByUserId("jenisa-001")).thenReturn(dummyWallet());

        mockMvc.perform(get("/wallet/jenisa-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("jenisa-001"))
                .andExpect(jsonPath("$.balance").value(100000.00));
    }

    @Test
    void getWallet_notFound_returns404() throws Exception {
        when(walletService.getWalletByUserId("unknown"))
                .thenThrow(new WalletNotFoundException("Wallet not found"));

        mockMvc.perform(get("/wallet/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createWallet_returns201() throws Exception {
        when(walletService.createWallet(eq("jenisa-001"), any())).thenReturn(dummyWallet());

        String body = """
                {
                    "userId": "jenisa-001",
                    "initialBalance": 100000
                }
                """;

        mockMvc.perform(post("/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("jenisa-001"));
    }

    @Test
    void topUp_returns200() throws Exception {
        when(walletService.topUp(eq("jenisa-001"), any())).thenReturn(dummyWallet());

        String body = """
                {
                    "userId": "jenisa-001",
                    "amount": 50000
                }
                """;

        mockMvc.perform(post("/wallet/topup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("jenisa-001"));
    }

    @Test
    void withdraw_returns200() throws Exception {
        when(walletService.withdraw(eq("jenisa-001"), any())).thenReturn(dummyWallet());

        String body = """
                {
                    "userId": "jenisa-001",
                    "amount": 20000
                }
                """;

        mockMvc.perform(post("/wallet/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("jenisa-001"));
    }
}