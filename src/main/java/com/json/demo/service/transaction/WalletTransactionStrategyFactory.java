package com.json.demo.service.transaction;

import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class WalletTransactionStrategyFactory {
    private final Map<WalletTransactionType, WalletTransactionStrategy> strategies;

    public WalletTransactionStrategyFactory(List<WalletTransactionStrategy> availableStrategies) {
        this.strategies = new EnumMap<>(WalletTransactionType.class);
        for (WalletTransactionStrategy strategy : availableStrategies) {
            strategies.put(strategy.type(), strategy);
        }
    }

    public WalletTransactionStrategy get(WalletTransactionType type) {
        WalletTransactionStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported wallet transaction type: " + type);
        }
        return strategy;
    }
}
