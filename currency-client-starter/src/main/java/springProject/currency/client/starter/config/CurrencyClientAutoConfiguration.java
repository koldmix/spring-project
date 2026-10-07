package springProject.currency.client.starter.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import springProject.currency.client.starter.client.ExchangeRateClient;
import springProject.currency.client.starter.health.HttpHealthIndicator;
import springProject.currency.client.starter.service.CurrencyService;

@ConditionalOnProperty(
        prefix = "currency-client-starter",
        name = "enabled",
        havingValue = "true"
)
@AutoConfiguration
@EnableConfigurationProperties(CurrencyClientProperties.class)
@EnableFeignClients(clients = ExchangeRateClient.class)
@Import(CurrencyClientResilienceConfiguration.class)
public class CurrencyClientAutoConfiguration {

    @Bean
    public CurrencyService currencyService(ExchangeRateClient exchangeRateClient) {
        return new CurrencyService(exchangeRateClient);
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
