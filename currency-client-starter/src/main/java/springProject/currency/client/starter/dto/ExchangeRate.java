package springProject.currency.client.starter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExchangeRate {
    private String date;
    private String base;
    private String quote;
    private BigDecimal rate;
}
