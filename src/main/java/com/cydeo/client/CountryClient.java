package com.cydeo.client;

import com.cydeo.dto.countries.Country;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(url = "https://restcountries.com/v3.1", name = "COUNTRY-CLIENT")
public interface CountryClient {
    
    @GetMapping("/all")
    List<Country> getCountries(@RequestParam(name = "fields", required = true) String fields);
    
}
