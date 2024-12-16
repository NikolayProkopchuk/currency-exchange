package com.prokopchuk.currrency.exchange.service.client.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import com.prokopchuk.currrency.exchange.config.ClientConfig;
import com.prokopchuk.currrency.exchange.service.client.ClientCurrencyExchangeRatesDto;

class CurrencyExchangeClientImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ClientConfig clientConfig;

    @InjectMocks
    private CurrencyExchangeClientImpl currencyExchangeClientImpl;

    public CurrencyExchangeClientImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnCurrencyExchangesForValidTargetCurrencyCodes() {
        Set<String> targetCurrencyCodes = Set.of("EUR", "USD");
        String expectedPath = "https://api.example.com";
        String accessKey = "testAccessKey";
        String baseCurrency = "GBP";
        String targetCurrencies = String.join(",", targetCurrencyCodes);

        when(clientConfig.getRequestPath()).thenReturn(expectedPath);
        when(clientConfig.getAccessKey()).thenReturn(accessKey);
        when(clientConfig.getBaseCurrencyCode()).thenReturn(baseCurrency);

        // Act
        currencyExchangeClientImpl.getCurrencyExchanges(targetCurrencyCodes);
        verify(restTemplate, times(1))
          .getForObject(expectedPath, ClientCurrencyExchangeRatesDto.class, accessKey, baseCurrency, targetCurrencies);
        verify(clientConfig, times(1)).getRequestPath();
        verify(clientConfig, times(1)).getAccessKey();
        verify(clientConfig, times(1)).getBaseCurrencyCode();
    }
}
