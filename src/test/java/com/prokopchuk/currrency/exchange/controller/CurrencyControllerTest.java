package com.prokopchuk.currrency.exchange.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.prokopchuk.currrency.exchange.controller.dto.req.CreateCurrencyDto;
import com.prokopchuk.currrency.exchange.controller.dto.resp.CurrencyDto;
import com.prokopchuk.currrency.exchange.service.CurrencyService;


@SpringBootTest
@AutoConfigureMockMvc
class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CurrencyService currencyService;

    @Test
    void createCurrency_ValidRequest_ReturnsCreatedCurrency() throws Exception {
        // Arrange
        CreateCurrencyDto requestDto = new CreateCurrencyDto("USD", "United States Dollar");
        CurrencyDto responseDto = new CurrencyDto(1, "USD", "United States Dollar");

        when(currencyService.create(Mockito.any(CreateCurrencyDto.class))).thenReturn(responseDto);

        // Act
        ResultActions result = mockMvc.perform(post("/currencies")
          .contentType(MediaType.APPLICATION_JSON)
          .content("{\"code\": \"USD\", \"name\": \"United States Dollar\"}"));

        // Assert
        result.andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(responseDto.id()))
          .andExpect(jsonPath("$.code").value(responseDto.code()))
          .andExpect(jsonPath("$.name").value(responseDto.name()));
    }

    @Test
    void createCurrency_InvalidCurrencyCode_ReturnsBadRequest() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(post("/currencies")
          .contentType(MediaType.APPLICATION_JSON)
          .content("{\"code\": \"INVALID\", \"name\": \"Invalid Currency\"}"));

        // Assert
        result.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message").value("Wrong currency code: INVALID"));
    }

    @Test
    void createCurrency_MissingCode_ReturnsBadRequest() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(post("/currencies")
          .contentType(MediaType.APPLICATION_JSON)
          .content("{\"name\": \"Invalid Currency\"}"));

        // Assert
        result.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message").value("Currency code is required"));
    }

    @Test
    void createCurrency_MissingName_ReturnsBadRequest() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(post("/currencies")
          .contentType(MediaType.APPLICATION_JSON)
          .content("{\"code\": \"USD\"}"));

        // Assert
        result.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message").value("Currency name is required"));
    }
}