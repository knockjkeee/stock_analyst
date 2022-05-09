package org.rostovpavel.webservice.services.indicators;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.rostovpavel.base.dto.StocksDTO;
import org.rostovpavel.base.models.BB.BollingerBands;
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
public class BBService implements IndicatorService{
    public static final int SMA_DAY_LENGTH = 20;
    public static final int SMA_MULTIPLIER_LENGTH = 2;

    private final MAService maService;

    @Override
    public BollingerBands getData(StocksDTO data) {
        List<BigDecimal> closeArr = new ArrayList<>();
        List<BigDecimal> upBandArr = new ArrayList<>();
        List<BigDecimal> loBandArr = new ArrayList<>();
        List<BigDecimal> bandArr = new ArrayList<>();
        List<BigDecimal> sma20Arr = new ArrayList<>();

        IntStream.range(0, 2).forEach(i -> {
            List<Stock> stocks = IntStream.range(i, SMA_DAY_LENGTH + i).mapToObj(e -> data.getStocks().get(e)).collect(Collectors.toList());
            BigDecimal sma20 = maService.getSMA(stocks, SMA_DAY_LENGTH);

            List<BigDecimal> collect = stocks.stream().map(stock -> {
                if (stock.getClose().compareTo(sma20) == 0){
                    return BigDecimal.ZERO;
                }else{
                    BigDecimal subtract = stock.getClose().subtract(sma20);
                    return subtract.multiply(subtract);
                }
            }).collect(Collectors.toList());
            BigDecimal res = collect
                    .stream().reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal sd = BigDecimal.valueOf(Math.sqrt(res.doubleValue() / SMA_DAY_LENGTH));
            BigDecimal upperBand = sma20.add((sd).multiply(BigDecimal.valueOf(SMA_MULTIPLIER_LENGTH)));
            BigDecimal lowerBand = sma20.subtract((sd).multiply(BigDecimal.valueOf(SMA_MULTIPLIER_LENGTH)));
            BigDecimal bandWidth = upperBand.subtract(lowerBand);

            closeArr.add(stocks.get(0).getClose());
            sma20Arr.add(sma20);
            upBandArr.add(upperBand);
            loBandArr.add(lowerBand);
            bandArr.add(bandWidth);
        });

        BigDecimal procent = bandArr.get(0).divide(closeArr.get(0), 5, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);

        return BollingerBands.builder()
                .middleBand(sma20Arr.get(0).setScale(2, RoundingMode.HALF_UP))
                .upperBand(upBandArr.get(0).setScale(3, RoundingMode.HALF_UP))
                .lowerBand(loBandArr.get(0).setScale(3, RoundingMode.HALF_UP))
                .widthBand(bandArr.get(0).setScale(3, RoundingMode.HALF_UP))
                ._key(compareBBToBuySell(closeArr, upBandArr, loBandArr).getValue())
                .wbProcent(procent.setScale(2, RoundingMode.HALF_UP))
                .build();
    }


    private Signal compareBBToBuySell(List<BigDecimal> close, List<BigDecimal> upData, List<BigDecimal> loData) {
        if ((close.get(0).compareTo(loData.get(0)) < 0)
                && (close.get(1).compareTo(loData.get(1)) >= 0)) {
            return Signal.BUY;
        }
        if ((close.get(0).compareTo(upData.get(0)) > 0)
                && (close.get(1).compareTo(upData.get(1)) <= 0)) {
            return Signal.SELL;
        }
        return Signal.NONE;
    }
}
