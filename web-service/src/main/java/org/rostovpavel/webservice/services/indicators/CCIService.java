package org.rostovpavel.webservice.services.indicators;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.rostovpavel.base.dto.StocksDTO;
import org.rostovpavel.base.models.purchases.CCI.CommodityChannel;
import org.rostovpavel.base.models.Signal;
import org.rostovpavel.base.models.Stock;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log4j2
@Service
@RequiredArgsConstructor
public class CCIService implements IndicatorService {
    public static final int RANGE = 20;
    private static final int UPLINE = 100;
    private static final int DOWNLINE = -100;

    @Override
    public CommodityChannel getData(@NotNull StocksDTO date) {
        List<Stock> stocks = date.getStocks();
        List<BigDecimal> cciArr = new ArrayList<>();

        IntStream.range(0,2).forEach(i -> {
            List<Double> tpData = IntStream.range(i, RANGE + i).mapToObj(index -> {
                Stock stock = stocks.get(index);
                return stock.getHigh().doubleValue() + stock.getLow().doubleValue() + stock.getClose().doubleValue();
            }).collect(Collectors.toList());

            double smaOfTP = getSmaOfTP(tpData);
            double meanDeviation = getMeanDeviation(tpData, smaOfTP);
            double cci = getData(tpData, smaOfTP, meanDeviation);
            cciArr.add(BigDecimal.valueOf(cci));
        });

        return CommodityChannel.builder()
                .currentCCI(cciArr.get(0).setScale(2, RoundingMode.HALF_UP))
                .upLine(UPLINE)
                .downLine(DOWNLINE)
                ._key(compareCCIToBuySell(cciArr).getValue())
                .build();
    }

    private Signal compareCCIToBuySell(@NotNull List<BigDecimal> cci) {

        if ((cci.get(0).compareTo(BigDecimal.valueOf(DOWNLINE)) < 0) && (cci.get(1).compareTo(BigDecimal.valueOf(DOWNLINE)) >= 0)) {
            return Signal.BUY;
        }
        if ((cci.get(0).compareTo(BigDecimal.valueOf(UPLINE)) > 0) && (cci.get(1).compareTo(BigDecimal.valueOf(UPLINE)) <= 0)) {
            return Signal.SELL;
        }
        return Signal.NONE;
    }

    @Contract(pure = true)
    private double getData(@NotNull List<Double> tpData, double smaOfTP, double meanDeviation) {
        return (tpData.get(0) - smaOfTP) / (0.015 * meanDeviation);
    }

    private double getMeanDeviation(@NotNull List<Double> tpData, double smaOfTP) {
        return tpData.stream().mapToDouble(e -> Math.abs(smaOfTP - e)).sum() / tpData.size();
    }

    private double getSmaOfTP(@NotNull List<Double> tpData) {
        return tpData.stream().mapToDouble(i -> i).sum() / tpData.size();
    }
}
