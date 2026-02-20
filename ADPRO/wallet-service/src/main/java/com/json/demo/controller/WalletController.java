package com.json.demo.controller;

import com.json.demo.model.Wallet;
import com.json.demo.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/{userId}")
    public String getWallet(@PathVariable String userId, Model model) {
        Wallet wallet = walletService.getOrCreateWallet(userId);
        model.addAttribute("wallet", wallet);
        return "wallet";
    }

    @PostMapping("/{userId}/topup")
    public String topUp(@PathVariable String userId,
                        @RequestParam BigDecimal amount) {
        walletService.topUp(userId, amount);
        return "redirect:/wallet/" + userId;
    }
}
