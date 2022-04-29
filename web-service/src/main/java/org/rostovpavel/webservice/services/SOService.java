package org.rostovpavel.webservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.rostovpavel.base.dto.StocksDTO;
import org.rostovpavel.base.models.SO.StochasticOscillator;
import org.rostovpavel.base.models.Stock;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
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
        List<BigDecimal> modeK = IntStream.range(0, 5).mapToObj(index -> getK(index, data)).collect(Collectors.toList());
        BigDecimal K = modeK.stream().limit(3).reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(3), 5, RoundingMode.UP);
        List<BigDecimal> collectK = IntStream.range(0, 3).mapToObj(index -> getKRes(index, modeK)).collect(Collectors.toList());
        BigDecimal D = collectK.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(collectK.size()), 5, RoundingMode.UP);

        return StochasticOscillator.builder()
                .upLine(UPLINE)
                .currentK(K.setScale(2, RoundingMode.HALF_UP))
                .currentD(D.setScale(2, RoundingMode.HALF_UP))
                .downLine(DOWNLINE)
                .build();
    }

    private BigDecimal getKRes(int index, List<BigDecimal> data){
        List<BigDecimal> collect = IntStream.range(index, 3 + index).mapToObj(data::get).collect(Collectors.toList());

        return collect.stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(collect.size()), 6, RoundingMode.UP);
    }

    private BigDecimal getK(int i, StocksDTO data) {
        List<Stock> collect = IntStream.range(i, DEEP_DAY + i).mapToObj(index -> data.getStocks().get(index)).collect(Collectors.toList());
        List<BigDecimal> low = collect.stream().map(Stock::getLow).collect(Collectors.toList());
        List<BigDecimal> high = collect.stream().map(Stock::getHigh).collect(Collectors.toList());
        BigDecimal max = high.stream().max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
        BigDecimal min = low.stream().min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
        Stock stock = data.getStocks().get(i);
        BigDecimal closeLowestLow = stock.getClose().subtract(min);
        BigDecimal highestHighLowestLow = max.subtract(min);

        return (closeLowestLow.divide(highestHighLowestLow, 6, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(100));
    }
}