package org.rostovpavel.webservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.rostovpavel.base.dto.StocksDTO;
import org.rostovpavel.base.models.MACD.MovingAverageConvergenceDivergence;
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
public class MACDService {
    private static final int EMA12DAY = 12;
    private static final int EMA26DAY = 26;
    private static final int SIGNALCONST = 9;


    public MovingAverageConvergenceDivergence getMACD(StocksDTO data) {
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
                .build();
    }

    private List<BigDecimal> getDifferenceBetweenCollect(List<BigDecimal> whereData, List<BigDecimal> whatData) {
        return IntStream.range(0, whatData.size()).mapToObj(index -> {
            return whereData.get(index).subtract(whatData.get(index));
        }).collect(Collectors.toList());
    }


    private List<BigDecimal> getSliceByDay(List<BigDecimal> stocks, int val) {
        AtomicReference<Double> result = new AtomicReference<>(0.0);
        return IntStream.range(1, stocks.size()).mapToObj(index -> {
            double cVal = stocks.get(index).doubleValue();
//            =(B17*(2/(12+1))+C16*(1-(2/(12+1))))
//            =(B31*(2/(26+1)) + D30*(1-(2/(26+1))))
            result.set(cVal * (2 / (val + 1.0)) + result.get() * (1 - (2 / (val + 1.0))));

            return BigDecimal.valueOf(result.get())
                    .setScale(6, RoundingMode.HALF_UP );
        }).collect(Collectors.toList());
    }
}
