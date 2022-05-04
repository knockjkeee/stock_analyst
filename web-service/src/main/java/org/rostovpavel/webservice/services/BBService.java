package org.rostovpavel.webservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.rostovpavel.base.dto.StocksDTO;
import org.rostovpavel.base.models.BB.BollingerBands;
import org.rostovpavel.base.models.Stock;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class BBService {
    private final MAService maService;

    public static final int SMA_DAY_LENGTH = 20;
    public static final int SMA_MULTIPLIER_LENGTH = 2;

    public BollingerBands getBollingerBands(StocksDTO data) {
        List<Stock> stocks = data.getStocks().stream().limit(SMA_DAY_LENGTH).collect(Collectors.toList());
        BigDecimal sma20 = maService.getSMA(stocks, SMA_DAY_LENGTH);

//        AtomicReference<Double> res = new AtomicReference<>(0.0);
//        stocks.forEach(stock -> {
//            BigDecimal subtract = stock.getClose().subtract(sma20);
//            BigDecimal multiply = subtract.multiply(subtract);
//            res.set(multiply.setScale(4, RoundingMode.HALF_UP).doubleValue() + res.get());
////            res.set(Math.pow(stock.getClose().doubleValue() - sma20.doubleValue(), 2));
//    });

        List<BigDecimal> collect = stocks.stream().map(stock -> {
            if (stock.getClose().compareTo(sma20) == 0){
                return BigDecimal.ZERO;
            }else{
                BigDecimal subtract = stock.getClose().subtract(sma20);
                BigDecimal multiply = subtract.multiply(subtract);
                return multiply;
            }
        }).collect(Collectors.toList());
        BigDecimal res = collect
                .stream().reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal sd = BigDecimal.valueOf(Math.sqrt(res.doubleValue() / SMA_DAY_LENGTH));
        BigDecimal upperBand = sma20.add((sd).multiply(BigDecimal.valueOf(SMA_MULTIPLIER_LENGTH)));
        BigDecimal lowerBand = sma20.subtract((sd).multiply(BigDecimal.valueOf(SMA_MULTIPLIER_LENGTH)));
        BigDecimal bandWidth = upperBand.subtract(lowerBand);

        return BollingerBands.builder()
                .middleBand(sma20.setScale(2, RoundingMode.HALF_UP))
                .upperBand(upperBand.setScale(3, RoundingMode.HALF_UP))
                .lowerBand(lowerBand.setScale(3, RoundingMode.HALF_UP))
                .widthBand(bandWidth.setScale(3, RoundingMode.HALF_UP))
                .build();
    }


//
//    public static double calculateSD(double numArray[]){
//        double sum = 0.0, standardDeviation = 0.0;
//        int length = numArray.length;
//        for(double num : numArray){
//            sum +=num;
//        }
//        double mean = sum / length;
//
//        for(double num:numArray){
//            standardDeviation +=Math.pow(num -mean,2);
//        }
//        return Math.sqrt(standardDeviation/length);
//    }

}
