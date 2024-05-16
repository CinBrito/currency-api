package br.com.ada.currencyapi.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.ada.currencyapi.domain.ConvertCurrencyRequest;
import br.com.ada.currencyapi.domain.ConvertCurrencyResponse;
import br.com.ada.currencyapi.domain.Currency;
import br.com.ada.currencyapi.domain.CurrencyRequest;
import br.com.ada.currencyapi.domain.CurrencyResponse;
import br.com.ada.currencyapi.exception.CoinNotFoundException;
import br.com.ada.currencyapi.exception.CurrencyException;
import br.com.ada.currencyapi.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    final private RestTemplate restTemplate = new RestTemplate();

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

    private BigDecimal getAmount(ConvertCurrencyRequest request) throws CoinNotFoundException, JsonProcessingException {
        Currency from = currencyRepository.findByName(request.getFrom());

        if (Objects.isNull(from)) {
            throw new CoinNotFoundException(String.format("Coin not found: %s", request.getFrom()));
        }

        String exchangeSource = "https://economia.awesomeapi.com.br/json/last/";
        ResponseEntity<String> response
                = restTemplate.getForEntity(exchangeSource + request.getFrom() + "-" + request.getTo() , String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        JsonNode subRoot = root.path(request.getFrom()+request.getTo());
        JsonNode currencyRate = subRoot.path("high");
        BigDecimal exchange = new BigDecimal(currencyRate.asText());

        return request.getAmount().multiply(exchange);
    }


}
