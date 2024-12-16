package com.prokopchuk.currrency.exchange.data.access.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prokopchuk.currrency.exchange.data.access.entity.CurrencyEntity;

public interface CurrencyRepository extends JpaRepository<CurrencyEntity, Long> {
}
