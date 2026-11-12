
package springProject.currency.client.starter.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.currency-client.cache")
public class CacheProperties {

    private Duration entryTtl = Duration.ofMinutes(30);
}
