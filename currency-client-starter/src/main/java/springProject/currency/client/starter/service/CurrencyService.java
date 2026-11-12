package springProject.currency.client.starter.service;

import lombok.RequiredArgsConstructor;
import springProject.currency.client.starter.client.ExchangeRateClient;
import springProject.currency.client.starter.dto.ExchangeRate;
import org.springframework.cache.annotation.Cacheable;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class CurrencyService {
    private final ExchangeRateClient exchangeRateClient;
    private final MetricsService metricsService;

    @Cacheable(
            cacheNames = "exchangeRates",
            key = "#fromCurrency.toUpperCase() + '_' + #toCurrency.toUpperCase()",
            condition = "!#fromCurrency.equalsIgnoreCase(#toCurrency)"
    )
    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency){
        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            return BigDecimal.ONE;
        }

        metricsService.incrementCounter(
                MetricsService.CURRENCY_EXCHANGE_RATE_REQUESTS);

        ExchangeRate response = exchangeRateClient.getExchangeRate(fromCurrency, toCurrency);

        return response.getRate();
    }
}
