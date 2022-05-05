package org.rostovpavel.webservice.services.indicators;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.rostovpavel.base.dto.StocksDTO;
import org.rostovpavel.base.models.CCI.CommodityChannel;
import org.rostovpavel.base.models.Stock;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    public CommodityChannel getData(StocksDTO date) {
        List<Stock> stocks = date.getStocks();
        List<Double> tpData = IntStream.range(0, RANGE).mapToObj(index -> {
            Stock stock = stocks.get(index);
            return stock.getHigh().doubleValue() + stock.getLow().doubleValue() + stock.getClose().doubleValue();
        }).collect(Collectors.toList());

        double smaOfTP = getSmaOfTP(tpData);
        double meanDeviation = getMeanDeviation(tpData, smaOfTP);
        double cci = getData(tpData, smaOfTP, meanDeviation);

        return CommodityChannel.builder()
                .currentCCI(new BigDecimal(cci).setScale(2, RoundingMode.HALF_UP))
                .upLine(UPLINE)
                .downLine(DOWNLINE)
                .build();

    }

    private double getData(List<Double> tpData, double smaOfTP, double meanDeviation) {
        return (tpData.get(0) - smaOfTP) / (0.015 * meanDeviation);
    }

    private double getMeanDeviation(List<Double> tpData, double smaOfTP) {
        return tpData.stream().mapToDouble(e -> Math.abs(smaOfTP - e)).sum() / tpData.size();
    }

    private double getSmaOfTP(List<Double> tpData) {
        return tpData.stream().mapToDouble(i -> i).sum() / tpData.size();
    }


}
