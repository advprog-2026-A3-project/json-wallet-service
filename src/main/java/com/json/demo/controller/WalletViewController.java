package com.json.demo.controller;

import com.json.demo.model.UserWallet;
import com.json.demo.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;

@Controller
@RequestMapping("/view/wallet")
@RequiredArgsConstructor
public class WalletViewController {

    private final WalletService walletService;

    @GetMapping("/{userId}")
    public String dashboard(@PathVariable String userId, Model model) {
        UserWallet wallet = walletService.getWalletByUserId(userId);
        model.addAttribute("wallet", wallet);
        return "wallet";
    }

     @PostMapping("/{userId}/topup")
    public String topUp(@PathVariable String userId, @RequestParam BigDecimal amount, Model model) {
        UserWallet wallet = walletService.topUp(userId, amount);
        model.addAttribute("wallet", wallet);
        return "wallet";
    }

    @PostMapping("/{userId}/withdraw")
    public String withdraw(@PathVariable String userId, @RequestParam BigDecimal amount, Model model) {
        UserWallet wallet = walletService.withdraw(userId, amount);
        model.addAttribute("wallet", wallet);
        return "wallet";
    }
}