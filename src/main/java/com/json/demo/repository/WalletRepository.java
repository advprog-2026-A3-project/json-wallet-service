package com.json.demo.repository;

import com.json.demo.model.UserWallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<UserWallet, UUID> {
    Optional<UserWallet> findByUserId(String userId);
}