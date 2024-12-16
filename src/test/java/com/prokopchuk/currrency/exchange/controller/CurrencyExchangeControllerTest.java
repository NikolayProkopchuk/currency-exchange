package com.prokopchuk.currrency.exchange.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.prokopchuk.currrency.exchange.controller.dto.resp.CurrencyExchangeDto;
import com.prokopchuk.currrency.exchange.controller.dto.resp.SimpleCurrencyDto;
import com.prokopchuk.currrency.exchange.service.CurrencyExchangeService;

@SpringBootTest
@AutoConfigureMockMvc
class CurrencyExchangeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CurrencyExchangeService currencyExchangeService;

    @Test
    void getCurrencyExchanges_ShouldReturnListOfCurrencyExchanges_WhenCurrencyIdIsValid() throws Exception {
        Integer currencyId = 1;

        CurrencyExchangeDto exchange1 = new CurrencyExchangeDto(
          new SimpleCurrencyDto(1, "USD"),
          new SimpleCurrencyDto(2, "EUR"),
          new BigDecimal("0.85"),
          LocalDateTime.now());
        CurrencyExchangeDto exchange2 = new CurrencyExchangeDto(
          new SimpleCurrencyDto(1, "USD"),
          new SimpleCurrencyDto(3, "GBP"),
          new BigDecimal("0.75"),
          LocalDateTime.now());

        when(currencyExchangeService.getCurrencyExchanges(currencyId))
          .thenReturn(List.of(exchange1, exchange2));

        mockMvc.perform(get("/currencies/{currencyId}/rates", currencyId)
            .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(2)))
          .andExpect(jsonPath("$[0].base.id", is(exchange1.base().id())))
          .andExpect(jsonPath("$[0].base.code", is(exchange1.base().code())))
          .andExpect(jsonPath("$[0].rate", is(exchange1.rate().doubleValue())));
    }

    @Test
    void getCurrencyExchanges_ShouldReturnEmptyList_WhenNoExchangesFound() throws Exception {
        Integer currencyId = 2;

        when(currencyExchangeService.getCurrencyExchanges(currencyId))
          .thenReturn(List.of());

        mockMvc.perform(get("/currencies/{currencyId}/rates", currencyId)
            .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(0)));
    }
}
