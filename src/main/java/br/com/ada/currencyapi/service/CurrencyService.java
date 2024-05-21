package br.com.ada.currencyapi.service;

import java.math.BigDecimal;
import java.util.*;

import br.com.ada.currencyapi.client.AwesomeApiClient;
import br.com.ada.currencyapi.domain.*;
import br.com.ada.currencyapi.domain.Currency;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.ada.currencyapi.exception.CoinNotFoundException;
import br.com.ada.currencyapi.exception.CurrencyException;
import br.com.ada.currencyapi.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final AwesomeApiClient awesomeApiClient;

    private final CurrencyRepository currencyRepository;



    public List<CurrencyResponse> get() {
        List<Currency> currencies = currencyRepository.findAll();
        List<CurrencyResponse> dtos = new ArrayList<>();

        currencies.forEach((currency) -> dtos.add(CurrencyResponse.builder()
                .label("%s - %s".formatted(currency.getId(), currency.getName()))
                .build()));

        return dtos;
    }

    public Long create(CurrencyRequest request) throws CurrencyException {
        Currency currency = currencyRepository.findByName(request.getName());

        if (Objects.nonNull(currency)) {
            throw new CurrencyException("Coin already exists");
        }

        Currency saved = currencyRepository.save(Currency.builder()
                .name(request.getName())
                .description(request.getDescription())
                .exchanges(request.getExchanges())
                .build());
        return saved.getId();
    }

    public void delete(Long id) {
        currencyRepository.deleteById(id);
    }

    public ConvertCurrencyResponse convert(ConvertCurrencyRequest request) throws CoinNotFoundException, JsonProcessingException {
        BigDecimal amount = getAmount(request);
        return ConvertCurrencyResponse.builder()
                .amount(amount)
                .build();

    }

    private BigDecimal getAmount(ConvertCurrencyRequest request) throws CoinNotFoundException {

        String coins = request.getFrom() + "-" + request.getTo();
        ResponseEntity<Map<String, AwesomeApiResponse>> response = awesomeApiClient.getExchange(coins);

        if (Objects.requireNonNull(response.getBody()).isEmpty()) {
            throw new CoinNotFoundException(String.format("There was an error processing request for coins: %s and %s", request.getFrom(), request.getTo()));
        }

        Map<String, AwesomeApiResponse> currencyData = response.getBody();
        BigDecimal exchange = new BigDecimal(currencyData.get(coins.replace("-","")).getHigh());

        return request.getAmount().multiply(exchange);
    }

}
