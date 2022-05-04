package org.rostovpavel.webservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.rostovpavel.base.dto.StocksDTO;
import org.rostovpavel.base.exception.StockNotFoundException;
import org.rostovpavel.base.models.MA.EMA;
import org.rostovpavel.base.models.MA.MovingAverage;
import org.rostovpavel.base.models.MA.SMA;
import org.rostovpavel.base.models.Signal;
import org.rostovpavel.base.models.Stock;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Log4j2
@Service
@RequiredArgsConstructor
public class MAService {
    private final static int[] LENGTH_MA = new int[]{20, 50, 100};

    public MovingAverage getMovingAverage(StocksDTO date) {
        List<SMA> smaData = Arrays.asList(SMA.builder().build(), SMA.builder().build());
        List<EMA> emaData = Arrays.asList(EMA.builder().build(), EMA.builder().build());
        List<Stock> stocks = date.getStocks();
        IntStream.range(0, 2).forEach(index -> {
            Arrays.stream(LENGTH_MA)
                    .forEach(len -> {
                        BigDecimal sma = getSMA(stocks, len);
                        BigDecimal ema = getEMA(stocks, sma, len);
                        switch (len) {
                            case 20 -> {
                                smaData.get(index).setSma20(sma);
                                emaData.get(index).setEma20(ema);
                            }
                            case 50 -> {
                                smaData.get(index).setSma50(sma);
                                emaData.get(index).setEma50(ema);
                            }
                            case 100 -> {
                                smaData.get(index).setSma100(sma);
                                emaData.get(index).setEma100(ema);
                            }
                        }
                    });
        });
        setSignal(smaData, emaData, stocks);
        return MovingAverage.builder().ema(emaData.get(0)).sma(smaData.get(0)).build();
    }

    private void setSignal(List<SMA> smaData, List<EMA> emaData, List<Stock> stocks) {
        IntStream.range(0, 1).forEach(index -> {
            Arrays.stream(LENGTH_MA)
                    .forEach(len -> {
                        switch (len) {
                            case 20 -> {
                                smaData.get(index).setSignalSma20(
                                        compareCloseWithMAToBuySell(stocks,
                                                smaData.get(0).getSma20(), smaData.get(1).getSma20()).getValue()
                                );

                                emaData.get(index).setSignalEma20(
                                        compareCloseWithMAToBuySell(stocks,
                                                emaData.get(0).getEma20(), emaData.get(1).getEma20()).getValue()
                                );
                            }
                            case 50 -> {
                                smaData.get(index).setSignalSma50(
                                        compareCloseWithMAToBuySell(stocks,
                                                smaData.get(0).getSma50(), smaData.get(1).getSma50()).getValue()
                                );
                                emaData.get(index).setSignalEma50(
                                        compareCloseWithMAToBuySell(stocks,
                                                emaData.get(0).getEma50(), emaData.get(1).getEma50()).getValue()
                                );
                            }
                            case 100 -> {
                                smaData.get(index).setSignalSma100(
                                        compareCloseWithMAToBuySell(stocks,
                                                smaData.get(0).getSma100(), smaData.get(1).getSma100()).getValue()
                                );
                                emaData.get(index).setSignalEma100(
                                        compareCloseWithMAToBuySell(stocks,
                                                emaData.get(0).getEma100(), emaData.get(1).getEma100()).getValue()
                                );
                            }
                        }
                    });
        });
    }


    private Signal compareCloseWithMAToBuySell(List<Stock> stocks, BigDecimal curMA, BigDecimal preMA) {
        if ((stocks.get(0).getClose().compareTo(curMA) > 0) && (stocks.get(1).getClose().compareTo(preMA) <= 0)) {
            return Signal.BUY;
        }
        if ((stocks.get(0).getClose().compareTo(curMA) < 0) && (stocks.get(1).getClose().compareTo(preMA) >= 0)) {
            return Signal.SELL;
        }
        return Signal.NONE;
    }


    public BigDecimal getSMA(List<Stock> stocks, int length) {
        return stocks
                .stream()
                .limit(length)
                .map(Stock::getClose)
                .reduce(BigDecimal::add)
                .orElseThrow(() -> new StockNotFoundException("Error MA"))
                .divide(BigDecimal.valueOf(length), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal getEMA(List<Stock> stocks, BigDecimal sma, int length) {
        BigDecimal key = new BigDecimal(2).divide(new BigDecimal(length)
                .add(new BigDecimal(1)), 5, RoundingMode.HALF_UP);

        return (stocks.get(0).getClose().multiply(key))
                .add((sma.multiply(new BigDecimal(1).subtract(key)))).setScale(2, RoundingMode.HALF_UP);
    }

}
