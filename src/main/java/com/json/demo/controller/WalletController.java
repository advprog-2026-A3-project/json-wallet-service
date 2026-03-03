package com.json.demo.controller;

import com.json.demo.model.UserWallet;
import com.json.demo.service.WalletService;
import com.json.demo.web.dto.CreateWalletRequest;
import com.json.demo.web.dto.TopUpRequest;
import com.json.demo.web.dto.UserWalletResponse;
import com.json.demo.web.dto.WithdrawRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserWalletResponse> getWallet(@PathVariable String userId) {
        UserWallet wallet = walletService.getWalletByUserId(userId);
        return ResponseEntity.ok(toResponse(wallet));
    }

    @PostMapping
    public ResponseEntity<UserWalletResponse> createWallet(@Validated @RequestBody CreateWalletRequest request) {
        UserWallet wallet = walletService.createWallet(request.userId(), request.initialBalance());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(wallet));
    }

    @PostMapping("/topup")
    public ResponseEntity<UserWalletResponse> topUp(@Validated @RequestBody TopUpRequest request) {
        UserWallet wallet = walletService.topUp(request.userId(), request.amount());
        return ResponseEntity.ok(toResponse(wallet));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<UserWalletResponse> withdraw(@Validated @RequestBody WithdrawRequest request) {
        UserWallet wallet = walletService.withdraw(request.userId(), request.amount());
        return ResponseEntity.ok(toResponse(wallet));
    }

    private static UserWalletResponse toResponse(UserWallet wallet) {
        return new UserWalletResponse(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getBalance(),
                wallet.getCreatedAt(),
                wallet.getUpdatedAt()
        );
    }
}
