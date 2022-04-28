package org.rostovpavel.webservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.rostovpavel.base.dto.StocksDTO;
import org.rostovpavel.base.models.SO.StochasticOscillator;
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
public class SOService {
    private static final int DEEP_DAY = 14;
    private static final int UPLINE = 80;
    private static final int DOWNLINE = 20;


    public StochasticOscillator getSO(StocksDTO data) {
        List<Double> collect = IntStream.range(0, 3).mapToObj(index -> {
            return getK(index, data);
        }).collect(Collectors.toList());

        Double K = collect.get(0);
        double D = collect.stream().mapToDouble(Double::doubleValue).sum() / collect.size();
        return StochasticOscillator.builder()
                .upLine(UPLINE)
                .currentK(new BigDecimal(K).setScale(2, RoundingMode.HALF_UP))
                .currentD(new BigDecimal(D).setScale(2, RoundingMode.HALF_UP))
                .downLine(DOWNLINE)
                .build();
    }

    private double getK(int i, StocksDTO data) {
        List<Double> high = IntStream.range(i, DEEP_DAY + i).mapToObj(index -> {
            return data.getStocks().get(index).getHigh().doubleValue();
        }).collect(Collectors.toList());

        List<Double> low = IntStream.range(0, DEEP_DAY).mapToObj(index -> {
            return data.getStocks().get(index).getLow().doubleValue();
        }).collect(Collectors.toList());

        double max = high.stream().mapToDouble(Double::doubleValue).max().getAsDouble();
        double min = low.stream().mapToDouble(Double::doubleValue).min().getAsDouble();

        Stock stock = data.getStocks().get(0);
        double result = (stock.getClose().doubleValue() - min) / (max - min) * 100;
        log.info(result);
        return result;
    }
}
