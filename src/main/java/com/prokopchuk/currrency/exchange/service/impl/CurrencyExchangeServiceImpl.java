package com.prokopchuk.currrency.exchange.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.prokopchuk.currrency.exchange.controller.dto.resp.CurrencyExchangeDto;
import com.prokopchuk.currrency.exchange.controller.dto.resp.SimpleCurrencyDto;
import com.prokopchuk.currrency.exchange.data.access.entity.CurrencyEntity;
import com.prokopchuk.currrency.exchange.data.access.entity.CurrencyExchangeRateEntity;
import com.prokopchuk.currrency.exchange.data.access.repository.CurrencyExchangeRepository;
import com.prokopchuk.currrency.exchange.data.access.repository.CurrencyRepository;
import com.prokopchuk.currrency.exchange.service.CurrencyExchangeService;
import com.prokopchuk.currrency.exchange.service.client.ClientCurrencyExchangeRatesDto;
import com.prokopchuk.currrency.exchange.service.client.CurrencyExchangeClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {

    private final Map<Integer, List<CurrencyExchangeDto>> cash = new HashMap<>();

    private final CurrencyRepository currencyRepository;
    private final CurrencyExchangeRepository currencyExchangeRepository;
    private final CurrencyExchangeClient currencyExchangeClient;

    @Override
    public List<CurrencyExchangeDto> getCurrencyExchanges(Integer currencyId) {
        return cash.get(currencyId);
    }

    @Scheduled(cron = "0 0 * * * *")
    public void updateCurrencyExchanges() {
        Map<String, CurrencyEntity> codeCurrencyMap = currencyRepository.findAll().stream()
          .collect(Collectors.toMap(CurrencyEntity::getCode, e -> e));
        Set<String> currencyCodes = codeCurrencyMap.keySet();
        ClientCurrencyExchangeRatesDto ratesDto =
          currencyExchangeClient.getCurrencyExchanges(currencyCodes);
        if (ratesDto == null || !ratesDto.success()) {
            log.error("Something was wrong with API, currencies exchange rates were not updated");
            return;
        }
        log.info("Get currencies exchange rates from API : {}", ratesDto);
        cash.clear();
        for (var entry : ratesDto.rates().entrySet()) {
            List<CurrencyExchangeRateEntity> entities =
              calculateExchangeRatesForCurrency(entry, ratesDto.rates(), codeCurrencyMap);
            CurrencyEntity baseCurrencyEntity = codeCurrencyMap.get(entry.getKey());
            for (CurrencyExchangeRateEntity entity : entities) {
                currencyExchangeRepository.findByBaseIdAndTargetId(entity.getBase().getId(), entity.getTarget().getId())
                  .ifPresentOrElse(persistedEntity -> {
                      persistedEntity.setRate(entity.getRate());
                      currencyExchangeRepository.save(persistedEntity);
                    },
                    () -> currencyExchangeRepository.save(entity));
            }
            List<CurrencyExchangeDto> dtos = convertEntityToDtos(entities);

            log.info("Update cache for currency {} : {}", baseCurrencyEntity.getCode(), dtos);
            cash.put(baseCurrencyEntity.getId(), dtos);
        }
    }

    private List<CurrencyExchangeDto> convertEntityToDtos(List<CurrencyExchangeRateEntity> entities) {
        return entities.stream()
          .map(entity -> {
              var baseCurrencyDto = new SimpleCurrencyDto(entity.getBase().getId(), entity.getBase().getCode());
              var targetCurrencyDto = new SimpleCurrencyDto(entity.getTarget().getId(), entity.getTarget().getCode());
              return new CurrencyExchangeDto(baseCurrencyDto, targetCurrencyDto, entity.getRate(), LocalDateTime.now());
          })
          .toList();
    }

    private List<CurrencyExchangeRateEntity> calculateExchangeRatesForCurrency(
      Map.Entry<String, BigDecimal> baseCurrencyExchangeEntry,
      Map<String, BigDecimal> exchangeRatesMap,
      Map<String, CurrencyEntity> codeCurrencyEntityMap) {
        CurrencyEntity baseCurrencyEntity = codeCurrencyEntityMap.get(baseCurrencyExchangeEntry.getKey());
        return exchangeRatesMap.entrySet().stream()
          .filter(entry -> !baseCurrencyExchangeEntry.getKey().equals(entry.getKey()))
          .map(entry -> {
              BigDecimal exchangeRate =
                entry.getValue().divide(baseCurrencyExchangeEntry.getValue(), 4, RoundingMode.HALF_EVEN);
              CurrencyEntity targetCurrency = codeCurrencyEntityMap.get(entry.getKey());
              return new CurrencyExchangeRateEntity(baseCurrencyEntity, targetCurrency, exchangeRate);
          })
          .toList();
    }

}
