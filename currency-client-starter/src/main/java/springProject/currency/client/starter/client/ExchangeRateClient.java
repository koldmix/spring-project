package springProject.currency.client.starter.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import springProject.currency.client.starter.dto.ExchangeRate;

@FeignClient(
        name = "exchangeRateClient",
        url = "${app.currency-client.base-url}"
)
public interface ExchangeRateClient {

    @GetMapping("${app.currency-client.rate-path}/{base}/{quote}")
    ExchangeRate getExchangeRate(@PathVariable("base") String base,
                                 @PathVariable("quote") String quote);
}