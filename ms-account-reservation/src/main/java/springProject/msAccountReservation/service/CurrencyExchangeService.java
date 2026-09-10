package springProject.msAccountReservation.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import springProject.currency.client.starter.service.CurrencyService;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CurrencyExchangeService {
    private final CurrencyService currencyService;
    private static final Logger logger = LoggerFactory.getLogger(CurrencyExchangeService.class);


    public BigDecimal getRate(String fromCurrency, String toCurrency) {
        try {
            return currencyService.getExchangeRate(fromCurrency, toCurrency);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Не удалось получить курс валют");
            throw e;
        }
    }
}
