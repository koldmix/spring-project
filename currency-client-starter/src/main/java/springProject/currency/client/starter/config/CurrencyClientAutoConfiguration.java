package springProject.currency.client.starter.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import springProject.currency.client.starter.service.CurrencyService;

@AutoConfiguration
@EnableConfigurationProperties(CurrencyClientProperties.class)
public class CurrencyClientAutoConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public CurrencyService currencyService(RestTemplate restTemplate,
                                           CurrencyClientProperties properties) {
        return new CurrencyService(restTemplate, properties);
    }
}
