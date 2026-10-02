package springProject.currency.client.starter.config;

import feign.Request;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

public class CurrencyClientResilienceConfiguration {

    @Bean
    public Retry currencyClientRetry(CurrencyClientProperties properties) {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(properties.getRetry().getMaxAttempts())
                .waitDuration(properties.getRetry().getWaitDuration())
                .build();

        return Retry.of("currencyClient", config);
    }

    @Bean
    public RateLimiter currencyClientRateLimiter(
            CurrencyClientProperties properties
    ) {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(
                        properties.getRateLimiter().getLimitForPeriod()
                )
                .limitRefreshPeriod(
                        properties.getRateLimiter().getLimitRefreshPeriod()
                )
                .timeoutDuration(
                        properties.getRateLimiter().getTimeoutDuration()
                )
                .build();

        return RateLimiter.of("currencyClient", config);
    }

    @Bean
    public Request.Options currencyClientRequestOptions(
            CurrencyClientProperties properties
    ) {
        return new Request.Options(
                properties.getTimeout().getConnectTimeout().toMillis(),
                TimeUnit.MILLISECONDS,
                properties.getTimeout().getReadTimeout().toMillis(),
                TimeUnit.MILLISECONDS,
                true
        );
    }
}