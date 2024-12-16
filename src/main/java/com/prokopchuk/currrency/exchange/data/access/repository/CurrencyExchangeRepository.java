package com.prokopchuk.currrency.exchange.data.access.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prokopchuk.currrency.exchange.data.access.entity.CurrencyExchangeId;
import com.prokopchuk.currrency.exchange.data.access.entity.CurrencyExchangeRateEntity;

public interface CurrencyExchangeRepository extends JpaRepository<CurrencyExchangeRateEntity, CurrencyExchangeId> {
    Optional<CurrencyExchangeRateEntity> findByBaseIdAndTargetId(Integer baseId, Integer targetId);
}
