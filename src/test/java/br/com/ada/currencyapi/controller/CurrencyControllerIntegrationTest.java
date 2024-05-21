package br.com.ada.currencyapi.controller;

import br.com.ada.currencyapi.client.AwesomeApiClient;
import br.com.ada.currencyapi.domain.Currency;
import br.com.ada.currencyapi.domain.CurrencyRequest;
import br.com.ada.currencyapi.repository.CurrencyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
public class CurrencyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CurrencyRepository currencyRepository;

    @MockBean
    private AwesomeApiClient awesomeApiClient;


    @Test
    void testGetCurrencyReturn200() throws Exception {
        assertEquals(0, currencyRepository.count());

        currencyRepository.save(new Currency(1L, "LCS", "Moeda do lucas", null));

        mockMvc.perform(get("/currency"))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(1, currencyRepository.count()));

        currencyRepository.deleteAll();
    }

    @Test
    void testCreateCurrencyReturn201() throws Exception {
        var request = new CurrencyRequest("LCS", "Moeda do lucas", null);

        var content = new ObjectMapper().writeValueAsString(request);

        mockMvc.perform(
                        post("/currency")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(content)
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$").isNotEmpty())
                .andDo(print());

        currencyRepository.deleteAll();
    }

    @Test
    void testConvertCurrencyThrowsCoinNotFoundExceptionReturn404() throws Exception {

        Mockito.when(awesomeApiClient.getExchange(any()))
                .thenReturn(new ResponseEntity<>(Collections.emptyMap(), HttpStatus.NOT_FOUND));

        mockMvc.perform(
                get("/currency/convert?from=USD&to=LCS&amount=10")
        ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("There was an error processing request for coins: USD and LCS"));
    }

    @Test
    void testCreateCurrencyThrowsCurrencyExceptionReturn500() throws Exception {
        var request = new CurrencyRequest("LCS", "Moeda do lucas", null);

        currencyRepository.save(new Currency(1L, "LCS", "Moeda do lucas", null));

        var content = new ObjectMapper().writeValueAsString(request);

        mockMvc.perform(
                        post("/currency")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(content)
                ).andExpect(status().isInternalServerError())
                .andDo(print());

        currencyRepository.deleteAll();
    }

    // TODO: Implementar testes
    @Test
    void testConvertCurrencyThrowsCoinNotFoundExceptionForExchangesReturn404() throws Exception {

        currencyRepository.save(new Currency(1L, "LCS", "Moeda do lucas", null));

        currencyRepository.save(new Currency(2L, "USD", "Dolar", null));

        mockMvc.perform(
                get("/currency/convert?from=USD&to=LCS&amount=10")
        ).andExpect(status().isNotFound());
    }
}