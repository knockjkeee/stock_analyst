package org.rostovpavel.webservice.services.indicators;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.rostovpavel.base.dto.StocksDTO;
import org.rostovpavel.base.exception.StockNotFoundException;
import org.rostovpavel.base.models.power.ATR.AverageTrueRange;
import org.rostovpavel.base.models.Signal;
import org.rostovpavel.base.models.Stock;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Log4j2
@Service
@RequiredArgsConstructor
public class ATRService implements IndicatorService {
    private static final int[] DEEP_DAY = new int[]{14, 13}; //+1
    private List<BigDecimal> pubAT;

    @Override
    public AverageTrueRange getData(@NotNull StocksDTO data) {
        List<Stock> stocks = IntStream.range(0, data.getStocks().size() - 1)
                .mapToObj(i -> data.getStocks().get(i))
                .collect(Collectors.toList());

        List<BigDecimal> atArr = getTR(stocks);
        pubAT = new ArrayList<>(atArr);
        List<BigDecimal> atrArr = getATR(atArr, DEEP_DAY[1], DEEP_DAY[0]);

        BigDecimal stopLoseLong = data.getStocks().get(1).getClose().subtract(atrArr.get(1).multiply(new BigDecimal(3))).setScale(3, RoundingMode.HALF_UP);

        BigDecimal stopLoseSort = data.getStocks().get(1).getClose().add(atrArr.get(1).multiply(new BigDecimal(3))).setScale(3, RoundingMode.HALF_UP);

        return AverageTrueRange.builder()
                .atr(atrArr.get(0).setScale(3, RoundingMode.HALF_UP))
                .stopLoseLong(stopLoseLong)
                .stopLoseShort(stopLoseSort)
                ._key(compareATRToBuySell(atrArr).getValue())
                .build();
    }

    public List<BigDecimal> getATR(List<Stock> stocks, int firstLength) {
        List<BigDecimal> atArr = getTR(stocks);
        atArr.add(BigDecimal.valueOf(0));
        List<BigDecimal> atr = IntStream.range(0, atArr.size() - firstLength).mapToObj(i -> {
            List<BigDecimal> collect = IntStream.range(i, i + firstLength).mapToObj(index -> atArr.get(index)).collect(Collectors.toList());
            return collect.stream()
                    .limit(firstLength)
                    .reduce(BigDecimal::add)
                    .orElseThrow(() -> new StockNotFoundException("Error MA"))
                    .divide(BigDecimal.valueOf(firstLength), 4, RoundingMode.HALF_UP);
        }).collect(Collectors.toList());
        return atr;
    }

    private Signal compareATRToBuySell(@NotNull List<BigDecimal> atr) {
        if ((atr.get(4).compareTo(atr.get(5)) > 0)
                && (atr.get(3).compareTo(atr.get(4)) > 0)
                && (atr.get(2).compareTo(atr.get(3)) > 0)
                && (atr.get(1).compareTo(atr.get(2)) > 0)
                && (atr.get(0).compareTo(atr.get(1)) > 0)) {
            return Signal.VAlHIGH;
        }
        if ((atr.get(3).compareTo(atr.get(4)) > 0)
                && (atr.get(2).compareTo(atr.get(3)) > 0)
                && (atr.get(1).compareTo(atr.get(2)) > 0)
                && (atr.get(0).compareTo(atr.get(1)) > 0)) {
            return Signal.VAlMEDIUM;
        }
        if ((atr.get(2).compareTo(atr.get(3)) > 0)
                && (atr.get(1).compareTo(atr.get(2)) > 0)
                && (atr.get(0).compareTo(atr.get(1)) > 0)) {
            return Signal.VAlLOW;
        }
        if ((atr.get(1).compareTo(atr.get(2)) > 0)
                && (atr.get(0).compareTo(atr.get(1)) > 0)) {
            return Signal.VAlSMALL;
        }
        return Signal.NONE;
    }

    private List<BigDecimal> getATR(List<BigDecimal> data, int secondLength, int firstLength) {
        List<BigDecimal> atArr = new ArrayList<>(data);
        Collections.reverse(atArr);
        AtomicReference<Double> result = new AtomicReference<>(0.0);
        List<BigDecimal> collect = IntStream.range(0, atArr.size()).mapToObj(index -> {
            BigDecimal cAT = atArr.get(index);
            double v = (result.get() * secondLength + cAT.doubleValue()) / firstLength;
            result.set(v);
            return new BigDecimal(v).setScale(3, RoundingMode.HALF_UP);
        }).collect(Collectors.toList());
        Collections.reverse(collect);
        return collect;
    }

    private List<BigDecimal> getTR(@NotNull List<Stock> stocks) {

        return IntStream.range(0, stocks.size() -1).mapToObj(index -> {
            BigDecimal hiLow = (stocks.get(index).getHigh().subtract(stocks.get(index).getLow())).abs();
            if (stocks.get(index).getHigh().compareTo(stocks.get(index).getLow()) == 0) {
                hiLow = BigDecimal.ZERO;
            }
            BigDecimal hiClose = (stocks.get(index).getHigh().subtract(stocks.get(index + 1).getClose())).abs();
            if (stocks.get(index).getHigh().compareTo(stocks.get(index + 1).getClose()) == 0) {
                hiClose = BigDecimal.ZERO;
            }
            BigDecimal closeLow = (stocks.get(index + 1).getClose().subtract(stocks.get(index).getLow())).abs();
            if (stocks.get(index).getLow().compareTo(stocks.get(index + 1).getClose()) == 0) {
                closeLow = BigDecimal.ZERO;
            }

            return Stream.of(hiLow, hiClose, closeLow).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
        }).collect(Collectors.toList());
    }

    public List<BigDecimal> getPubAT() {
        return pubAT;
    }
}
