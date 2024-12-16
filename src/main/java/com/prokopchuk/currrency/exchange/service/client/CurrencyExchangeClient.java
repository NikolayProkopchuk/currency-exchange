package com.prokopchuk.currrency.exchange.service.client;

import java.util.Set;

public interface CurrencyExchangeClient {
    ClientCurrencyExchangeRatesDto getCurrencyExchanges(Set<String> targetCurrencyCodes);
}
