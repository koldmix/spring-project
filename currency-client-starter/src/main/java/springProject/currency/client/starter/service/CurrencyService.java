package springProject.currency.client.starter.service;

import org.springframework.web.client.RestTemplate;
import springProject.currency.client.starter.config.CurrencyClientProperties;
import springProject.currency.client.starter.dto.ExchangeRate;

import java.math.BigDecimal;

public class CurrencyService {
    private final RestTemplate restTemplate;
    private final CurrencyClientProperties properties;

    public CurrencyService(RestTemplate restTemplate, CurrencyClientProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency){
        String url = properties.getBaseUrl()
                + "/v2/rate/"
                + fromCurrency
                + "/"
                + toCurrency;

        ExchangeRate response =
                restTemplate.getForObject(url, ExchangeRate.class);

        return response.getRate();
    }
}
