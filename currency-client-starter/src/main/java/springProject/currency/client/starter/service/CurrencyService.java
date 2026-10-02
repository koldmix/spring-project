package springProject.currency.client.starter.service;

import lombok.RequiredArgsConstructor;
import springProject.currency.client.starter.client.ExchangeRateClient;
import springProject.currency.client.starter.dto.ExchangeRate;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class CurrencyService {
    private final ExchangeRateClient exchangeRateClient;

    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency){
        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            return BigDecimal.ONE;
        }

        ExchangeRate response = exchangeRateClient.getExchangeRate(fromCurrency, toCurrency);

        return response.getRate();
    }
}
