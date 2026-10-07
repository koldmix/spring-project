package springProject.currency.client.starter.client;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import springProject.currency.client.starter.exception.CurrencyClientException;

@Slf4j
public class CurrencyClientErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {

        log.error("Currency API request failed. method={}, status={}, reason={}",
                methodKey,
                response.status(),
                response.reason());

        return new CurrencyClientException("Currency API returned HTTP " + response.status(),
                response.status());
    }
}