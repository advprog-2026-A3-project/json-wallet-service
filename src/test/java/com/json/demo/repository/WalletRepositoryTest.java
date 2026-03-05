package com.json.demo.repository;

import com.json.demo.model.UserWallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class WalletRepositoryTest {

    @Autowired
    private WalletRepository walletRepository;

    @Test
    void findByUserId_found() {
        UserWallet wallet = UserWallet.builder()
                .userId("repo-user-1")
                .balance(new BigDecimal("50000.00"))
                .build();
        walletRepository.save(wallet);

        Optional<UserWallet> result = walletRepository.findByUserId("repo-user-1");

        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo("repo-user-1");
        assertThat(result.get().getBalance()).isEqualByComparingTo("50000.00");
    }

    @Test
    void findByUserId_notFound() {
        Optional<UserWallet> result = walletRepository.findByUserId("nonexistent");
        assertThat(result).isEmpty();
    }

    @Test
    void save_persistsWallet() {
        UserWallet wallet = UserWallet.builder()
                .userId("repo-user-2")
                .balance(new BigDecimal("100000.00"))
                .build();

        UserWallet saved = walletRepository.save(wallet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUserId()).isEqualTo("repo-user-2");
    }
}