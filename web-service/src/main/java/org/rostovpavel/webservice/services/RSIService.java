package org.rostovpavel.webservice.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.rostovpavel.base.dto.StocksDTO;
import org.rostovpavel.base.models.RSI.RelativeStrengthIndex;
import org.rostovpavel.base.models.Stock;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

@Log4j2
@Service
@RequiredArgsConstructor
public class RSIService {
    private static final int [] DEEP_DAY = new int[] {14, 13}; //+1


    public RelativeStrengthIndex getRSI(StocksDTO data) {
        List<Stock> stocks = data.getStocks();
        Collections.reverse(stocks);
        double rs = getU(stocks) / Math.abs(getD(stocks));
        double rsi = 100 - (100 / (1 + rs));
        return RelativeStrengthIndex.builder().currentRsi(BigDecimal.valueOf(rsi)).build();
    }


    private double getU(List<Stock> stocks) {
        AtomicReference<Double> result = new AtomicReference<>(0.0);
        IntStream.range(1, stocks.size()).forEach(index -> {
            double cVal = stocks.get(index).getClose().doubleValue();
            double pVal = stocks.get(index - 1).getClose().doubleValue();
            double gVal = 0.0;
            if ((cVal - pVal) > 0) {
                gVal = cVal - pVal;
            } else {
                gVal = 0.0;
            }
            result.set(((result.get() * DEEP_DAY[1]) + gVal) / DEEP_DAY[0]);
        });
        return result.get();
    }

    private double getD(List<Stock> stocks) {
        AtomicReference<Double> result = new AtomicReference<>(0.0);
        IntStream.range(1, stocks.size()).forEach(index -> {
            double cVal = stocks.get(index).getClose().doubleValue();
            double pVal = stocks.get(index - 1).getClose().doubleValue();
            double gVal = 0.0;
            if ((cVal - pVal) < 0) {
                gVal = cVal - pVal;
            } else {
                gVal = 0.0;
            }
            result.set(((result.get() * DEEP_DAY[1]) + gVal) / DEEP_DAY[0]);
        });
        return result.get();
    }

}
