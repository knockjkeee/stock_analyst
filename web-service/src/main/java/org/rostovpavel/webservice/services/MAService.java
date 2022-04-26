package org.rostovpavel.webservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.rostovpavel.base.dto.StocksDTO;
import org.rostovpavel.base.exception.StockNotFoundException;
import org.rostovpavel.base.models.MA.EMA;
import org.rostovpavel.base.models.MA.MovingAverage;
import org.rostovpavel.base.models.MA.SMA;
import org.rostovpavel.base.models.Stock;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class MAService {
    private final static int[] LENGTH_MA = new int[]{20, 50, 100};

    public MovingAverage getMovingAverage(StocksDTO date) {
        List<Stock> stocks = date.getStocks();
        EMA emaRes = EMA.builder().build();
        SMA smaRes = SMA.builder().build();

        Arrays.stream(LENGTH_MA)
                .forEach(len -> {
                    BigDecimal sma = getSMA(stocks, len);
                    BigDecimal ema = getEMA(stocks, sma, len);
                    switch (len) {
                        case 20:
                            smaRes.setSma20(sma);
                            emaRes.setEma20(ema);
                            break;
                        case 50:
                            smaRes.setSma50(sma);
                            emaRes.setEma50(ema);
                            break;
                        case 100:
                            smaRes.setSma100(sma);
                            emaRes.setEma100(ema);
                            break;
                    }
                });
        return MovingAverage.builder().ema(emaRes).sma(smaRes).build();
    }

    private BigDecimal getSMA(List<Stock> stocks, int length) {
        return stocks
                .stream()
                .limit(length)
                .map(Stock::getClose)
                .reduce(BigDecimal::add)
                .orElseThrow(() -> new StockNotFoundException("Error MA"))
                .divide(BigDecimal.valueOf(length), 5, RoundingMode.HALF_UP);
    }


    private BigDecimal getEMA(List<Stock> stocks, BigDecimal sma, int length) {
        BigDecimal key = new BigDecimal(2).divide(new BigDecimal(length)
                .add(new BigDecimal(1)), 5, RoundingMode.HALF_UP);

        return (stocks.get(0).getClose().multiply(key))
                .add((sma.multiply(new BigDecimal(1).subtract(key))));
    }

}
