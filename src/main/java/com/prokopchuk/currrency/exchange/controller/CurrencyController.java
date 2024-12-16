package com.prokopchuk.currrency.exchange.controller;

import javax.xml.bind.ValidationException;
import java.util.Currency;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.prokopchuk.currrency.exchange.controller.dto.req.CreateCurrencyDto;
import com.prokopchuk.currrency.exchange.controller.dto.resp.CurrencyDto;
import com.prokopchuk.currrency.exchange.controller.dto.resp.ErrorDto;
import com.prokopchuk.currrency.exchange.service.CurrencyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(("/currencies"))
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping
    ResponseEntity<List<CurrencyDto>> getAllCurrencies() {
        List<CurrencyDto> dtos = currencyService.getAllCurrencies();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    ResponseEntity<CurrencyDto> createCurrency(@RequestBody CreateCurrencyDto dto) {
        if (dto.code() == null || dto.code().isBlank()) {
            throw new IllegalArgumentException("Currency code is required");
        }
        if (Currency.getAvailableCurrencies()
          .stream()
          .noneMatch(currency -> currency.getCurrencyCode().equals(dto.code()))) {
            throw new IllegalArgumentException("Wrong currency code: " + dto.code());
        }
        if (dto.name() == null || dto.name().isBlank()) {
            throw new IllegalArgumentException("Currency name is required");
        }
        CurrencyDto currencyDto = currencyService.create(dto);
        return ResponseEntity.ok(currencyDto);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorDto validationExceptionHandler(IllegalArgumentException ex) {
        return new ErrorDto(ex.getMessage());
    }
}
