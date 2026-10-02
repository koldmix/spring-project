package springProject.currency.client.starter.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import springProject.currency.client.starter.client.CurrencyClientErrorDecoder;

public class CurrencyClientFeignConfiguration {

    @Bean
    public ErrorDecoder currencyClientErrorDecoder() {
        return new CurrencyClientErrorDecoder();
    }
}