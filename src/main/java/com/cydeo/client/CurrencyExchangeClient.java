package com.cydeo.client;

import com.cydeo.dto.ExchangeRate;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;


@FeignClient(url = "https://cdn.jsdelivr.net", name = "CURRENCY-EXCHANGE")
public interface CurrencyExchangeClient {
    
    @GetMapping("npm/@fawazahmed0/currency-api@latest/v1/currencies/usd.json")
    ExchangeRate getExchangeRates();
    
    
}
