package springProject.currency.client.starter.service;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MetricsService {

    public static final String CURRENCY_EXCHANGE_RATE_REQUESTS =
            "currency.exchange.rate.requests";

    private final MeterRegistry meterRegistry;

    public void incrementCounter(String metricName) {
        meterRegistry.counter(metricName).increment();
    }
}
