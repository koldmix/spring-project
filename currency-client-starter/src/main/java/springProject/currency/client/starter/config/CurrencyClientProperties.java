package springProject.currency.client.starter.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "app.currency-client")
public class CurrencyClientProperties {

    private String baseUrl;
    private String ratePath;
    private RetryProperties retry;
    private TimeoutProperties timeout;
    private RateLimiterProperties rateLimiter;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RetryProperties {
        private int maxAttempts;
        private Duration waitDuration;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeoutProperties {
        private Duration connectTimeout;
        private Duration readTimeout;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RateLimiterProperties {
        private int limitForPeriod;
        private Duration limitRefreshPeriod;
        private Duration timeoutDuration;
    }
}
