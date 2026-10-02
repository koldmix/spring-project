package springProject.currency.client.starter.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.retry.Retry;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import springProject.currency.client.starter.client.ExchangeRateClient;
import springProject.currency.client.starter.service.CurrencyService;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class CurrencyClientAutoConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CurrencyClientAutoConfiguration.class,
                    FeignAutoConfiguration.class))
            .withPropertyValues("currency-client-starter.enabled=true",
                    "currency-client-starter.health.enabled=true",
                    "app.currency-client.base-url=https://api.frankfurter.dev",
                    "app.currency-client.retry.max-attempts=3",
                    "app.currency-client.retry.wait-duration=500ms",
                    "app.currency-client.timeout.connect-timeout=2s",
                    "app.currency-client.timeout.read-timeout=5s",
                    "app.currency-client.rate-limiter.limit-for-period=10",
                    "app.currency-client.rate-limiter.limit-refresh-period=1s",
                    "app.currency-client.rate-limiter.timeout-duration=100ms");

    @Test
    void shouldCreateCurrencyService() {

        contextRunner.run(context -> {
            Retry retry = context.getBean(Retry.class);

            assertThat(context)
                    .hasSingleBean(CurrencyService.class);

            assertThat(context)
                    .hasSingleBean(CurrencyClientProperties.class);

            assertThat(context.getBean(CurrencyClientProperties.class)
                    .getBaseUrl())
                    .isEqualTo("https://api.frankfurter.dev");

            assertThat(context)
                    .hasSingleBean(CurrencyService.class);

            assertThat(context)
                    .hasSingleBean(ExchangeRateClient.class);

            assertThat(retry.getRetryConfig().getMaxAttempts())
                    .isEqualTo(3);

            assertThat(context)
                    .hasSingleBean(RateLimiter.class);

            RateLimiter rateLimiter = context.getBean(RateLimiter.class);

            assertThat(rateLimiter.getRateLimiterConfig().getLimitForPeriod())
                    .isEqualTo(10);

            assertThat(rateLimiter.getRateLimiterConfig().getLimitRefreshPeriod())
                    .isEqualTo(Duration.ofSeconds(1));

            assertThat(rateLimiter.getRateLimiterConfig().getTimeoutDuration())
                    .isEqualTo(Duration.ofMillis(100));
        });
    }

    @Test
    void shouldNotCreateCurrencyServiceWhenDisabled() {
        contextRunner.withPropertyValues("currency-client-starter.enabled=false",
                        "app.currency-client.base-url=https://api.frankfurter.dev"
                ).run(context -> {assertThat(context)
                            .doesNotHaveBean(CurrencyService.class);
                });
    }
}
