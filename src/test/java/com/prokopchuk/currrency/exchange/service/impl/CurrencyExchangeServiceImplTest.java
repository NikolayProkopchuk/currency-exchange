package com.prokopchuk.currrency.exchange.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.prokopchuk.currrency.exchange.controller.dto.resp.CurrencyExchangeDto;
import com.prokopchuk.currrency.exchange.data.access.entity.CurrencyEntity;
import com.prokopchuk.currrency.exchange.data.access.repository.CurrencyExchangeRepository;
import com.prokopchuk.currrency.exchange.data.access.repository.CurrencyRepository;
import com.prokopchuk.currrency.exchange.service.client.ClientCurrencyExchangeRatesDto;
import com.prokopchuk.currrency.exchange.service.client.CurrencyExchangeClient;

@SpringBootTest
class CurrencyExchangeServiceImplTest {

    @MockitoBean
    private CurrencyRepository currencyRepository;

    @MockitoBean
    private CurrencyExchangeRepository currencyExchangeRepository;

    @MockitoBean
    private CurrencyExchangeClient currencyExchangeClient;

    @Test
    void updateCurrencyExchanges_WhenRatesAreSuccessful_ShouldUpdateCacheAndDatabase() {
        CurrencyEntity usd = new CurrencyEntity(1, "USD", "United States Dollar");
        CurrencyEntity eur = new CurrencyEntity(2, "EUR", "Euro");
        CurrencyEntity gbp = new CurrencyEntity(3, "GBP", "Pound Sterling");

        when(currencyRepository.findAll()).thenReturn(List.of(usd, eur, gbp));

        BigDecimal usdRate = new BigDecimal("1.0000");
        BigDecimal eurRate = new BigDecimal("0.8500");
        BigDecimal gbpRate = new BigDecimal("0.7500");

        Map<String, BigDecimal> rates = Map.of(
          "USD", usdRate,
          "EUR", eurRate,
          "GBP", gbpRate
        );

        when(currencyExchangeClient.getCurrencyExchanges(Set.of("USD", "EUR", "GBP")))
          .thenReturn(new ClientCurrencyExchangeRatesDto(usd.getCode(), rates, true));

        CurrencyExchangeServiceImpl service = new CurrencyExchangeServiceImpl(
          currencyRepository, currencyExchangeRepository, currencyExchangeClient
        );

        service.updateCurrencyExchanges();

        verify(currencyRepository, times(1)).findAll();
        verify(currencyExchangeClient, times(1)).getCurrencyExchanges(Set.of("USD", "EUR", "GBP"));

        verify(currencyExchangeRepository, times(3)).saveAll(anyList());

        List<CurrencyExchangeDto> cachedExchangesFromUsd = service.getCurrencyExchanges(usd.getId());
        assertThat(cachedExchangesFromUsd).isNotEmpty();
        assertThat(cachedExchangesFromUsd).hasSize(2);
        Set<String> targetCodesFromUsd =
          cachedExchangesFromUsd.stream().map(dto -> dto.target().code()).collect(Collectors.toSet());
        assertThat(targetCodesFromUsd.containsAll(Set.of("EUR", "GBP"))).isTrue();

        BigDecimal actualEuroRate = cachedExchangesFromUsd.stream()
          .filter(dto -> dto.target().code().equals("EUR"))
          .findFirst()
          .map(CurrencyExchangeDto::rate)
          .orElse(null);
        assertThat(actualEuroRate).isNotNull();
        assertThat(actualEuroRate).isEqualTo(eurRate);

        BigDecimal actualGbpRate = cachedExchangesFromUsd.stream()
          .filter(dto -> dto.target().code().equals("GBP"))
          .findFirst()
          .map(CurrencyExchangeDto::rate)
          .orElse(null);
        assertThat(actualGbpRate).isNotNull();
        assertThat(actualGbpRate).isEqualTo(gbpRate);
    }

    @Test
    void updateCurrencyExchanges_WhenRatesAreNull_ShouldNotClearCacheOrSaveToDatabase() {
        when(currencyRepository.findAll()).thenReturn(List.of());
        when(currencyExchangeClient.getCurrencyExchanges(anySet())).thenReturn(null);

        CurrencyExchangeServiceImpl service = new CurrencyExchangeServiceImpl(
          currencyRepository, currencyExchangeRepository, currencyExchangeClient
        );

        service.updateCurrencyExchanges();

        verify(currencyRepository, times(1)).findAll();
        verify(currencyExchangeClient, times(1)).getCurrencyExchanges(anySet());
        verify(currencyExchangeRepository, times(0)).saveAll(anyList());
    }

    @Test
    void updateCurrencyExchanges_WhenRatesAreUnsuccessful_ShouldNotClearCacheOrSaveToDatabase() {
        when(currencyRepository.findAll()).thenReturn(List.of());

        when(currencyExchangeClient.getCurrencyExchanges(anySet()))
          .thenReturn(new ClientCurrencyExchangeRatesDto("USD", Map.of(),false));

        CurrencyExchangeServiceImpl service = new CurrencyExchangeServiceImpl(
          currencyRepository, currencyExchangeRepository, currencyExchangeClient
        );

        service.updateCurrencyExchanges();

        verify(currencyRepository, times(1)).findAll();
        verify(currencyExchangeClient, times(1)).getCurrencyExchanges(anySet());
        verify(currencyExchangeRepository, times(0)).saveAll(anyList());
    }
}