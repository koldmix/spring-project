package springProject.currency.client.starter.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import springProject.currency.client.starter.client.ExchangeRateClient;

public class HttpHealthIndicator implements HealthIndicator {

    private final ExchangeRateClient exchangeRateClient;

    public HttpHealthIndicator(ExchangeRateClient exchangeRateClient) {
        this.exchangeRateClient = exchangeRateClient;
    }

    @Override
    public Health health() {
        try {
            exchangeRateClient.getExchangeRate("USD", "EUR");

            return Health.up().build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}