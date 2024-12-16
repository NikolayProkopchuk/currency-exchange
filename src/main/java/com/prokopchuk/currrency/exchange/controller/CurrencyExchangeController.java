package com.prokopchuk.currrency.exchange.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.prokopchuk.currrency.exchange.controller.dto.resp.CurrencyExchangeDto;
import com.prokopchuk.currrency.exchange.service.CurrencyExchangeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
class CurrencyExchangeController {

    private final CurrencyExchangeService currencyExchangeService;

    @GetMapping("/currencies/{currencyId}/rates")
    ResponseEntity<List<CurrencyExchangeDto>> getCurrencyExchanges(@PathVariable Integer currencyId) {
        return ResponseEntity.ok(currencyExchangeService.getCurrencyExchanges(currencyId));
    }
}
