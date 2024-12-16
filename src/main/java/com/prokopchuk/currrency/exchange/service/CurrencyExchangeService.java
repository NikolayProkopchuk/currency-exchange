package com.prokopchuk.currrency.exchange.service;

import java.util.List;

import com.prokopchuk.currrency.exchange.controller.dto.resp.CurrencyExchangeDto;

public interface CurrencyExchangeService {

    List<CurrencyExchangeDto> getCurrencyExchanges(Integer currencyId);
}
