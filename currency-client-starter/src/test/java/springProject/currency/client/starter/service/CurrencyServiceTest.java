package springProject.currency.client.starter.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import springProject.currency.client.starter.client.ExchangeRateClient;
import springProject.currency.client.starter.dto.ExchangeRate;
import springProject.currency.client.starter.health.HttpHealthIndicator;

import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CurrencyServiceTest {

    @Test
    void getExchangeRate() {
        // Arrange
        ExchangeRateClient exchangeRateClient = mock(ExchangeRateClient.class);

        ExchangeRate response = new ExchangeRate();
        response.setBase("USD");
        response.setQuote("EUR");
        response.setRate(new BigDecimal("0.85"));
        when(exchangeRateClient.getExchangeRate("USD", "EUR"))
                .thenReturn(response);
        CurrencyService currencyService = new CurrencyService(exchangeRateClient);
        // Act
        BigDecimal result = currencyService.getExchangeRate("USD", "EUR");
        // Assert
        assertEquals(new BigDecimal("0.85"), result);
        verify(exchangeRateClient) .getExchangeRate("USD", "EUR");
    }

    @Test
    void health_ExchangeRateApiAvailable_shouldReturnUp() {
        ExchangeRateClient client = mock(ExchangeRateClient.class);

        ExchangeRate response = new ExchangeRate();
        response.setRate(new BigDecimal("0.85"));

        when(client.getExchangeRate("USD", "EUR"))
                .thenReturn(response);

        HttpHealthIndicator indicator = new HttpHealthIndicator(client);

        Health health = indicator.health();

        assertThat(health.getStatus())
                .isEqualTo(Status.UP);

        verify(client)
                .getExchangeRate("USD", "EUR");
    }

    @Test
    void health_ExchangeRateApiUnavailable_shouldReturnDown() {
        ExchangeRateClient client = mock(ExchangeRateClient.class);

        when(client.getExchangeRate("USD", "EUR"))
                .thenThrow(new RuntimeException("API unavailable"));

        HttpHealthIndicator indicator = new HttpHealthIndicator(client);

        Health health = indicator.health();

        assertThat(health.getStatus())
                .isEqualTo(Status.DOWN);

        verify(client)
                .getExchangeRate("USD", "EUR");
    }

    @Test
    void getExchangeRate_SameCurrencies_shouldReturnOneWithoutClientCall() {
        ExchangeRateClient exchangeRateClient = mock(ExchangeRateClient.class);

        CurrencyService currencyService = new CurrencyService(exchangeRateClient);

        BigDecimal result = currencyService.getExchangeRate("USD", "USD");

        assertEquals(BigDecimal.ONE, result);

        verifyNoInteractions(exchangeRateClient);
    }
}