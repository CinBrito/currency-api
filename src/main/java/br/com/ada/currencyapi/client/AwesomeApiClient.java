package br.com.ada.currencyapi.client;

import br.com.ada.currencyapi.domain.AwesomeApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;

@FeignClient(name = "awesome-api", url = "https://economia.awesomeapi.com.br")
public interface AwesomeApiClient {

    @GetMapping("/json/last/{coins}")
    ResponseEntity<Map<String, AwesomeApiResponse>> getExchange(@PathVariable String coins);
}
