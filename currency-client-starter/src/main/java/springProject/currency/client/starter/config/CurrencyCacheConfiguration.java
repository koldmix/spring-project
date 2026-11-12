package springProject.currency.client.starter.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.Map;

@EnableCaching
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CacheProperties.class)
public class CurrencyCacheConfiguration {

    @Bean
    public RedisCacheManager redisCacheManager(
            RedisConnectionFactory redisConnectionFactory, CacheProperties cacheProperties) {

        RedisCacheConfiguration cacheConfiguration =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(cacheProperties.getEntryTtl());

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration)
                .withInitialCacheConfigurations(
                        Map.of("exchangeRates", cacheConfiguration))
                .build();
    }
}
