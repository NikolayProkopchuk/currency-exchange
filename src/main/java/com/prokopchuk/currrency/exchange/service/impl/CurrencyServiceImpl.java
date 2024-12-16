package com.prokopchuk.currrency.exchange.service.impl;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.prokopchuk.currrency.exchange.controller.dto.req.CreateCurrencyDto;
import com.prokopchuk.currrency.exchange.controller.dto.resp.CurrencyDto;
import com.prokopchuk.currrency.exchange.data.access.entity.CurrencyEntity;
import com.prokopchuk.currrency.exchange.data.access.repository.CurrencyRepository;
import com.prokopchuk.currrency.exchange.service.CurrencyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;

    @Override
    public List<CurrencyDto> getAllCurrencies() {
        return currencyRepository.findAll()
          .stream()
          .map(entity -> new CurrencyDto(entity.getId(), entity.getCode(), entity.getName()))
          .toList();
    }

    @Override
    public CurrencyDto create(CreateCurrencyDto createDto) {
        CurrencyEntity currency = new CurrencyEntity();
        currency.setCode(createDto.code());
        currency.setName(createDto.name());
        CurrencyEntity entity = currencyRepository.save(currency);
        return new CurrencyDto(entity.getId(), entity.getCode(), entity.getName());
    }

}
