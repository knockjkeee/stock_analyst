package org.rostovpavel.webservice.services.indicators;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.rostovpavel.base.dto.StocksDTO;
import org.rostovpavel.base.models.move.MACD.MovingAverageConvergenceDivergence;
import org.rostovpavel.base.models.Signal;
import org.rostovpavel.base.models.Stock;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log4j2
@Service
@RequiredArgsConstructor
public class MACDService implements IndicatorService {
    private static final int EMA12DAY = 12;
    private static final int EMA26DAY = 26;
    private static final int SIGNALCONST = 9;

    @Override
    public MovingAverageConvergenceDivergence getData(@NotNull StocksDTO data) {
        List<Stock> stocks = new ArrayList<>(data.getStocks());
        Collections.reverse(stocks);

        List<BigDecimal> close = stocks.stream().map(Stock::getClose).collect(Collectors.toList());
        List<BigDecimal> EMA12 = getSliceByDay(close, EMA12DAY);
        List<BigDecimal> EMA26 = getSliceByDay(close, EMA26DAY);
        List<BigDecimal> MACD = getDifferenceBetweenCollect(EMA12, EMA26);
        List<BigDecimal> signal = getSliceByDay(MACD, SIGNALCONST);
        BigDecimal histogram = MACD.get(MACD.size() - 1).subtract(signal.get(signal.size() - 1));

        return MovingAverageConvergenceDivergence.builder()
                .histogram(histogram.setScale(4, RoundingMode.HALF_UP))
                .signal(signal.get(signal.size() - 1).setScale(4, RoundingMode.HALF_UP))
                .MACD(MACD.get(MACD.size() - 1).setScale(4, RoundingMode.HALF_UP))
                ._key(compareMACDToBuySell(MACD, signal).getValue())
                .build();
    }


    private Signal compareMACDToBuySell(@NotNull List<BigDecimal> macd, @NotNull List<BigDecimal> signal) {
        if ((macd.get(macd.size() - 1).compareTo(signal.get(signal.size() - 1)) > 0)
                && (macd.get(macd.size() - 2).compareTo(signal.get(signal.size() - 2)) <= 0)) {
            return Signal.BUY;
        }
        if ((macd.get(macd.size() - 1).compareTo(signal.get(signal.size() - 1)) < 0)
                && (macd.get(macd.size() - 2).compareTo(signal.get(signal.size() - 2)) >= 0)) {
            return Signal.SELL;
        }

        if ((macd.get(macd.size() - 1).compareTo(BigDecimal.valueOf(0.0)) > 0)
                && (macd.get(macd.size() - 2).compareTo(BigDecimal.valueOf(0)) <= 0)) {
            return Signal.BUYPLUS;
        }
        if ((macd.get(macd.size() - 1).compareTo(BigDecimal.valueOf(0)) < 0)
                && (macd.get(macd.size() - 2).compareTo(BigDecimal.valueOf(0)) >= 0)) {
            return Signal.SELLMINUS;
        }
        return Signal.NONE;
    }

    private List<BigDecimal> getDifferenceBetweenCollect(List<BigDecimal> whereData, @NotNull List<BigDecimal> whatData) {
        return IntStream.range(0, whatData.size()).mapToObj(index -> whereData.get(index).subtract(whatData.get(index))).collect(Collectors.toList());
    }

    private List<BigDecimal> getSliceByDay(@NotNull List<BigDecimal> stocks, int val) {
        AtomicReference<Double> result = new AtomicReference<>(0.0);
        return IntStream.range(1, stocks.size()).mapToObj(index -> {
            double cVal = stocks.get(index).doubleValue();
            result.set(cVal * (2 / (val + 1.0)) + result.get() * (1 - (2 / (val + 1.0))));
            return BigDecimal.valueOf(result.get())
                    .setScale(8, RoundingMode.HALF_UP);
        }).collect(Collectors.toList());
    }
}
