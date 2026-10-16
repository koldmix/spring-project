
package springProject.currency.client.starter.service;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import springProject.currency.client.starter.client.ExchangeRateClient;
import springProject.currency.client.starter.config.CurrencyCacheConfiguration;
import springProject.currency.client.starter.dto.ExchangeRate;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(
        classes = CurrencyServiceRedisCacheTest.TestApplication.class
)
@Testcontainers
class CurrencyServiceRedisCacheTest {

    @Container
    static final GenericContainer<?> redis =
            new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
                    .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add(
                "spring.data.redis.port",
                () -> redis.getMappedPort(6379)
        );
    }

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private RedisCacheManager cacheManager;

    @MockitoBean
    private ExchangeRateClient exchangeRateClient;

    @Autowired
    private SimpleMeterRegistry meterRegistry;

    @TestConfiguration(proxyBeanMethods = false)
    static class TestConfig {

        @Bean
        SimpleMeterRegistry simpleMeterRegistry() {
            return new SimpleMeterRegistry();
        }
    }

    @Test
    void getExchangeRate_SamePairTwice_ShouldCallExternalApiOnce() {
        ExchangeRate response = new ExchangeRate();
        response.setRate(new BigDecimal("0.85"));

        when(exchangeRateClient.getExchangeRate("USD", "EUR"))
                .thenReturn(response);

        BigDecimal first = currencyService.getExchangeRate("USD", "EUR");
        BigDecimal second = currencyService.getExchangeRate("USD", "EUR");

        assertEquals(new BigDecimal("0.85"), first);
        assertEquals(first, second);

        verify(exchangeRateClient, times(1))
                .getExchangeRate("USD", "EUR");

        assertEquals(
                1.0,
                meterRegistry.counter("currency.exchange.rate.requests").count()
        );
    }

    @Test
    void cacheConfiguration_ShouldUseThirtyMinuteTtl() {
        Duration ttl = cacheManager.getCacheConfigurations()
                .get("exchangeRates")
                .getTtl();

        assertEquals(Duration.ofMinutes(30), ttl);
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import({
            CurrencyCacheConfiguration.class,
            CurrencyService.class,
            CurrencyServiceRedisCacheTest.TestConfig.class
    })
    static class TestApplication {
    }
}
