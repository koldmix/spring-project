package springProject.msAccountReservation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springProject.msAccountReservation.service.CurrencyExchangeService;

import java.math.BigDecimal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/currency")
public class CurrencyController {
    private final CurrencyExchangeService currencyExchangeService;
    @GetMapping("/rate")
    public BigDecimal getRate(@RequestParam String from, @RequestParam String to){
        return currencyExchangeService.getRate(from, to);
    }
}
