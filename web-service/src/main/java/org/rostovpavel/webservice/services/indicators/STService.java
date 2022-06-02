package org.rostovpavel.webservice.services.indicators;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.rostovpavel.base.dto.StocksDTO;
import org.rostovpavel.base.models.Signal;
import org.rostovpavel.base.models.Stock;
import org.rostovpavel.base.models.move.ST.SuperTrend;
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
public class STService implements IndicatorService {
    private static final int[] DEEP_DAY = new int[]{10, 30};
    private static final int[] FACTOR = new int[]{2, 5};

    private final ATRService atrService;

    @SneakyThrows
    @Override
    public SuperTrend getData(@NotNull StocksDTO data) {
        List<Stock> collect = data.getStocks();
        // generate
        List<BigDecimal> superTrendSecond = getSuperTrend(collect, DEEP_DAY[0], FACTOR[0]);
        List<BigDecimal> superTrendMain = getSuperTrend(collect, DEEP_DAY[1], FACTOR[1]);

        return SuperTrend.builder()
                .mainTrend(superTrendMain.get(0))
                ._keyMain(setKey(collect, superTrendMain).getValue())
                .secondTrend(superTrendSecond.get(0))
                ._keySecond(setKey(collect, superTrendSecond).getValue())
                .build();
    }

    private Signal setKey(@NotNull List<Stock> data, List<BigDecimal> trend) {
        //BigDecimal cClose = data.get(0).getClose();
        if (data.get(0).getClose().compareTo(trend.get(0)) < 0) {
            if (data.get(1).getClose().compareTo(trend.get(1)) > 0) {
                return Signal.SELLMINUS;
            } else {
                return Signal.SELL;
            }
        } else {
            if (data.get(1).getClose().compareTo(trend.get(1)) < 0) {
                return Signal.BUYPLUS;
            } else {
                return Signal.BUY;
            }
        }
        //return cClose.compareTo(trend.get(0)) < 0 ? Signal.SELL : Signal.BUY;
    }

    private @NotNull @Unmodifiable List<BigDecimal> getSuperTrend(List<Stock> collect, int deep, int multi) {
        List<BigDecimal> atr = atrService.getATR(collect, deep);
        List<Stock> stocks = collect.stream().limit(atr.size()).collect(Collectors.toList());

        List<BigDecimal> basicUpper = getBasicUpper(multi, atr, stocks);
        List<BigDecimal> basicLower = getBasicLower(multi, atr, stocks);

        List<Stock> tempStocks = stocks.stream().limit(basicLower.size()).collect(Collectors.toList());
        List<BigDecimal> tempBasicUpper = new ArrayList<>(basicUpper);
        List<BigDecimal> tempBasicLower = new ArrayList<>(basicLower);
        Collections.reverse(tempStocks);
        Collections.reverse(tempBasicUpper);
        Collections.reverse(tempBasicLower);

        AtomicReference<Double> temp = new AtomicReference<>(0.0);

        List<BigDecimal> finalUpperBand = getFinalUpperBand(tempStocks, tempBasicUpper, temp);
        temp.set(0.0);
        List<BigDecimal> finalLowerBand = getFinalLowerBand(tempStocks, tempBasicLower, temp);
        temp.set(finalUpperBand.get(0).doubleValue());
        List<BigDecimal> superTrend = generateSuperTrend(tempStocks, temp, finalUpperBand, finalLowerBand);
        Collections.reverse(superTrend);
        return superTrend;
    }

    @NotNull
    private List<BigDecimal> generateSuperTrend(List<Stock> tempStocks, AtomicReference<Double> temp, List<BigDecimal> finalUpperBand, @NotNull List<BigDecimal> finalLowerBand) {
        return IntStream.range(1, finalLowerBand.size()).mapToObj(index -> {
            if ((temp.get() == finalUpperBand.get(index - 1).doubleValue())
                    && (tempStocks.get(index + 1).getClose().compareTo(finalUpperBand.get(index)) <= 0)) {
                temp.set(finalUpperBand.get(index).doubleValue());
                return finalUpperBand.get(index);
            } else {
                if ((temp.get() == finalUpperBand.get(index - 1).doubleValue())
                        && (tempStocks.get(index + 1).getClose().compareTo(finalUpperBand.get(index)) >= 0)) {
                    temp.set(finalLowerBand.get(index).doubleValue());
                    return finalLowerBand.get(index);
                } else {
                    if ((temp.get() == finalLowerBand.get(index - 1).doubleValue())
                            && (tempStocks.get(index + 1).getClose().compareTo(finalLowerBand.get(index)) >= 0)) {
                        temp.set(finalLowerBand.get(index).doubleValue());
                        return finalLowerBand.get(index);
                    } else {
                        if ((temp.get() == finalLowerBand.get(index - 1).doubleValue())
                                && (tempStocks.get(index + 1).getClose().compareTo(finalLowerBand.get(index)) <= 0)) {
                            temp.set(finalUpperBand.get(index).doubleValue());
                            return finalUpperBand.get(index);
                        } else {
                            return BigDecimal.ZERO;
                        }
                    }
                }
            }
        }).collect(Collectors.toList());
    }

    @NotNull
    private List<BigDecimal> getFinalLowerBand(List<Stock> tempStocks, @NotNull List<BigDecimal> tempBasicLower, AtomicReference<Double> temp) {
        return IntStream.range(1, tempBasicLower.size()).mapToObj(index -> {
            if ((tempBasicLower.get(index).doubleValue() > temp.get())
                    || (tempStocks.get(index - 1).getClose().doubleValue() < temp.get())) {
                temp.set(tempBasicLower.get(index).doubleValue());
                return tempBasicLower.get(index).setScale(2, RoundingMode.HALF_UP);
            } else {
                return BigDecimal.valueOf(temp.get()).setScale(2, RoundingMode.HALF_UP);
            }
        }).collect(Collectors.toList());
    }

    @NotNull
    private List<BigDecimal> getFinalUpperBand(List<Stock> tempStocks, @NotNull List<BigDecimal> tempBasicUpper, AtomicReference<Double> temp) {
        return IntStream.range(1, tempBasicUpper.size()).mapToObj(index -> {
            if ((tempBasicUpper.get(index).doubleValue() < temp.get())
                    || (tempStocks.get(index - 1).getClose().doubleValue() > temp.get())) {
                temp.set(tempBasicUpper.get(index).doubleValue());
                return tempBasicUpper.get(index).setScale(2, RoundingMode.HALF_UP);
            } else {
                return BigDecimal.valueOf(temp.get()).setScale(2, RoundingMode.HALF_UP);
            }
        }).collect(Collectors.toList());
    }

    @NotNull
    private List<BigDecimal> getBasicLower(int multi, @NotNull List<BigDecimal> atr, List<Stock> stocks) {
        return IntStream.range(0, atr.size()).mapToObj(index -> (stocks.get(index).getHigh().add(stocks.get(index).getLow()))
                .divide(BigDecimal.valueOf(2), 5, RoundingMode.HALF_UP)
                .subtract(atr.get(index).multiply(BigDecimal.valueOf(multi)))
                .setScale(2, RoundingMode.HALF_UP)).collect(Collectors.toList());
    }

    @NotNull
    private List<BigDecimal> getBasicUpper(int multi, @NotNull List<BigDecimal> atr, List<Stock> stocks) {
        return IntStream.range(0, atr.size()).mapToObj(index -> (stocks.get(index).getHigh().add(stocks.get(index).getLow()))
                .divide(BigDecimal.valueOf(2), 5, RoundingMode.HALF_UP)
                .add(atr.get(index).multiply(BigDecimal.valueOf(multi)))
                .setScale(2, RoundingMode.HALF_UP)).collect(Collectors.toList());
    }
}
