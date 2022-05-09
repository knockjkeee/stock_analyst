package org.rostovpavel.webservice.services.indicators;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.rostovpavel.base.dto.StocksDTO;
import org.rostovpavel.base.models.purchases.RSI_SO.RelativeStrengthIndexStochastic;
import org.rostovpavel.base.models.Signal;
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
public class RSIStochService implements IndicatorService {
    private static final int RANGE = 14;
    private static final double UPLINE = 0.8;
    private static final double DOWNLINE = 0.2;

    private final RSIService rsiService;

    @Override
    public RelativeStrengthIndexStochastic getData(StocksDTO data) {
        List<BigDecimal> rsiStochArr  = new ArrayList<>();

        IntStream.range(0, 2).forEach(i -> {
            List<Double> collect = IntStream.range(i, RANGE + i).mapToObj(index -> rsiService.getData(index, data).getCurrentRSI().doubleValue()).collect(Collectors.toList());

            double min = collect.stream().mapToDouble(Double::doubleValue).min().getAsDouble();
            double max = collect.stream().mapToDouble(Double::doubleValue).max().getAsDouble();
            double stochRSI = (collect.get(0) - min) / (max - min);
            rsiStochArr.add(BigDecimal.valueOf(stochRSI));
        });

        return RelativeStrengthIndexStochastic.builder()
                .upLine(UPLINE)
                .currentStochRSI(rsiStochArr.get(0).setScale(2, RoundingMode.HALF_UP))
                .downLine(DOWNLINE)
                ._key(compareRSIStochToBuySell(rsiStochArr).getValue())
                .build();
    }


    private Signal compareRSIStochToBuySell(List<BigDecimal> rsiStoch) {

        if ((rsiStoch.get(0).compareTo(BigDecimal.valueOf(DOWNLINE)) < 0) && (rsiStoch.get(1).compareTo(BigDecimal.valueOf(DOWNLINE)) >= 0)) {
            return Signal.BUY;
        }
        if ((rsiStoch.get(0).compareTo(BigDecimal.valueOf(UPLINE)) > 0) && (rsiStoch.get(1).compareTo(BigDecimal.valueOf(UPLINE)) <= 0)) {
            return Signal.SELL;
        }
        return Signal.NONE;
    }
}
