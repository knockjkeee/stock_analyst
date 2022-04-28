package org.rostovpavel.webservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.rostovpavel.base.dto.StocksDTO;
import org.rostovpavel.base.models.RSI_SO.RelativeStrengthIndexStochastic;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log4j2
@Service
@RequiredArgsConstructor
public class RSIStochService {
    private static final int RANGE = 14;
    private static final double UPLINE = 0.8;
    private static final double DOWNLINE = 0.2;

    private final RSIService rsiService;

    public RelativeStrengthIndexStochastic getStochRSI(StocksDTO data) {
        List<Double> collect = IntStream.range(0, RANGE).mapToObj(index -> {
            return rsiService.getRSI(index, data).getCurrentRSI().doubleValue();
        }).collect(Collectors.toList());

//        List<Double> collect = Arrays.asList(57.97, 62.93, 63.26, 56.06, 62.38, 54.71, 50.42, 39.99
//                , 41.46, 41.87, 45.46, 37.3, 33.08, 37.77);
//        Collections.reverse(collect);

        double min = collect.stream().mapToDouble(Double::doubleValue).min().getAsDouble();
        double max = collect.stream().mapToDouble(Double::doubleValue).max().getAsDouble();
        double stochRSI = (collect.get(0) - min) / (max - min);
        return RelativeStrengthIndexStochastic.builder()
                .upLine(UPLINE)
                .currentStochRSI(new BigDecimal(stochRSI).setScale(2, RoundingMode.HALF_UP))
                .downLine(DOWNLINE)
                .build();
    }

}
