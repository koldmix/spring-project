package springProject.currency.client.starter.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import springProject.currency.client.starter.client.ExchangeRateClient;
import springProject.currency.client.starter.health.HttpHealthIndicator;
import springProject.currency.client.starter.service.CurrencyService;
import org.springframework.context.annotation.Import;

@ConditionalOnProperty(
        prefix = "currency-client-starter",
        name = "enabled",
        havingValue = "true"
)
@AutoConfiguration
@EnableConfigurationProperties(CurrencyClientProperties.class)
@EnableFeignClients(clients = ExchangeRateClient.class)
@Import({CurrencyClientResilienceConfiguration.class,
        CurrencyCacheConfiguration.class})
public class CurrencyClientAutoConfiguration {

    @Bean
    public CurrencyService currencyService(ExchangeRateClient exchangeRateClient,
                                           MeterRegistry meterRegistry) {
        return new CurrencyService(exchangeRateClient, meterRegistry);
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "currency-client-starter.health",
            name = "enabled",
            havingValue = "true"
    )
    public HttpHealthIndicator httpHealthIndicator(
            ExchangeRateClient exchangeRateClient) {
        return new HttpHealthIndicator(exchangeRateClient);
    }
}
