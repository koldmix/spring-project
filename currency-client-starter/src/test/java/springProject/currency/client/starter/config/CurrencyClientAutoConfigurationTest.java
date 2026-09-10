package springProject.currency.client.starter.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import springProject.currency.client.starter.service.CurrencyService;

import static org.assertj.core.api.Assertions.assertThat;

public class CurrencyClientAutoConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CurrencyClientAutoConfiguration.class))
            .withPropertyValues("app.currency-client.base-url=https://api.frankfurter.dev");

    @Test
    void shouldCreateCurrencyService() {
        contextRunner.run(context -> {
            assertThat(context)
                    .hasSingleBean(CurrencyService.class);

            assertThat(context)
                    .hasSingleBean(CurrencyClientProperties.class);

            assertThat(context.getBean(CurrencyClientProperties.class)
                    .getBaseUrl())
                    .isEqualTo("https://api.frankfurter.dev");
        });
    }
}
