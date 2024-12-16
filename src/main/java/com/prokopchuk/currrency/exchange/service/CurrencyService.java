package com.prokopchuk.currrency.exchange.service;

import java.util.List;

import com.prokopchuk.currrency.exchange.controller.dto.req.CreateCurrencyDto;
import com.prokopchuk.currrency.exchange.controller.dto.resp.CurrencyDto;
import com.prokopchuk.currrency.exchange.data.access.entity.CurrencyEntity;

public interface CurrencyService {

    List<CurrencyDto> getAllCurrencies();

    CurrencyDto create(CreateCurrencyDto createDto);
}
