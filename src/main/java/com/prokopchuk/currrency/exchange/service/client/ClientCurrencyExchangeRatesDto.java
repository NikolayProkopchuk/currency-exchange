package com.prokopchuk.currrency.exchange.service.client;

import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClientCurrencyExchangeRatesDto(String base, Map<String, BigDecimal> rates, Boolean success) {
}
