package springProject.currency.client.starter.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import springProject.currency.client.starter.config.CurrencyClientProperties;
import springProject.currency.client.starter.dto.ExchangeRate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CurrencyServiceTest {

    @Test
    void getExchangeRate() {
        // Arrange
        RestTemplate restTemplate = mock(RestTemplate.class);

        CurrencyClientProperties properties = new CurrencyClientProperties();
        properties.setBaseUrl("https://api.frankfurter.dev");

        ExchangeRate response = new ExchangeRate();
        response.setBase("USD");
        response.setQuote("EUR");
        response.setRate(new BigDecimal("0.85"));

        when(restTemplate.getForObject("https://api.frankfurter.dev/v2/rate/USD/EUR",
                ExchangeRate.class)).thenReturn(response);

        CurrencyService currencyService =
                new CurrencyService(restTemplate, properties);

        // Act
        BigDecimal result =
                currencyService.getExchangeRate("USD", "EUR");

        // Assert
        assertEquals(new BigDecimal("0.85"), result);

        verify(restTemplate).getForObject(
                "https://api.frankfurter.dev/v2/rate/USD/EUR", ExchangeRate.class);
    }
}