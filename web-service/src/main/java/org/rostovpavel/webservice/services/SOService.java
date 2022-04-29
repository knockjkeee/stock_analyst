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
        List<BigDecimal> collect = IntStream.range(0, 3).mapToObj(index -> getK(index, data)).collect(Collectors.toList());

        BigDecimal K = collect.get(0);
        BigDecimal D = (collect.stream().reduce(BigDecimal.ZERO, BigDecimal::add)).
                divide(BigDecimal.valueOf(collect.size()), 6, RoundingMode.HALF_UP);
        return StochasticOscillator.builder()
                .upLine(UPLINE)
                .currentK(K.setScale(4, RoundingMode.HALF_UP))
                .currentD(D.setScale(4, RoundingMode.HALF_UP))
                .downLine(DOWNLINE)
                .build();
    }

    private BigDecimal getK(int i, StocksDTO data) {
        List<BigDecimal> high = IntStream.range(i, DEEP_DAY + i).mapToObj(index -> data.getStocks().get(index).getHigh()).collect(Collectors.toList());

        List<BigDecimal> low = IntStream.range(0, DEEP_DAY).mapToObj(index -> data.getStocks().get(index).getLow()).collect(Collectors.toList());

        BigDecimal max = high.stream().max(BigDecimal::compareTo).get();
        BigDecimal min = low.stream().min(BigDecimal::compareTo).get();

        Stock stock = data.getStocks().get(0);
        return (stock.getClose().subtract(min)).divide((max.subtract(min)), 5, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100.0));
    }
}