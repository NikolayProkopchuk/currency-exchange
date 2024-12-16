package com.prokopchuk.currrency.exchange.service.client.impl;

import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.prokopchuk.currrency.exchange.config.ClientConfig;
import com.prokopchuk.currrency.exchange.service.client.ClientCurrencyExchangeRatesDto;
import com.prokopchuk.currrency.exchange.service.client.CurrencyExchangeClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CurrencyExchangeClientImpl implements CurrencyExchangeClient {

    private final RestTemplate restTemplate;
    private final ClientConfig clientConfig;

    @Override
    public ClientCurrencyExchangeRatesDto getCurrencyExchanges(Set<String> targetCurrencyCodes) {
        return restTemplate.getForObject(
          clientConfig.getRequestPath(),
          ClientCurrencyExchangeRatesDto.class,
          clientConfig.getAccessKey(), clientConfig.getBaseCurrencyCode(), String.join(",", targetCurrencyCodes));
    }
}
