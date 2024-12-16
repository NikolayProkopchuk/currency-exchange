package com.prokopchuk.currrency.exchange.controller.dto.resp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CurrencyExchangeDto (SimpleCurrencyDto base, SimpleCurrencyDto target, BigDecimal rate, LocalDateTime updatedAt) {
}
