package org.rostovpavel.webservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.rostovpavel.base.dto.StockDTO;
import org.rostovpavel.base.dto.StockDataDTO;
import org.rostovpavel.base.exception.StockNotFoundException;
import org.rostovpavel.base.models.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class StockService {
    //private final InvestApi api = InvestApi.create(System.getenv("token"));
    private final TinkoffService tinkoffService;

    public StockDTO getStockDataByTicker(String ticker) {
        String figi = tinkoffService.getCurrentFigi(ticker, "SPBXM");
        List<Stock> currentHistoricCandle = tinkoffService.getListStockHistoricCandlesByFigi(figi);
        return new StockDTO(currentHistoricCandle);
    }

    public StockDataDTO getStockDataByTikers(TickerData data){
        List<StockData> date = data.getTickers().stream()
                .map(ticker -> {
                    String figi = tinkoffService.getCurrentFigi(ticker, "SPBXM");
                    List<Stock> candle = tinkoffService.getListStockHistoricCandlesByFigi(figi);
                    return StockData.builder()
                            .name(ticker)
                            .candle(new StockDTO(candle))
                            .build();
                }).collect(Collectors.toList());
        return new StockDataDTO(date);
    }

    public void ema(){
        StockDTO clov = getStockDataByTicker("CLOV");
////        clov.getStocks().stream()
////                //.limit(20)
////                .flatMap(Collection::stream)
////                .map(Stock::getClose)
//                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b) );
        List<Stock> stocks = clov.getStocks();
        BigDecimal sma = stocks
                .stream()
                .limit(100)
                .map(Stock::getClose)
                .reduce(BigDecimal::add)
                .orElseThrow(() -> new StockNotFoundException("Error MA"))
                .divide(BigDecimal.valueOf(100));

//        double kk = 2/(4+1);
        BigDecimal key = new BigDecimal(2).divide(new BigDecimal(100).add(new BigDecimal(1)), 5, RoundingMode.HALF_UP);

        //TODO main
        BigDecimal ema =
                (stocks.get(0).getClose().multiply(key))
                .add((sma
                        .multiply(new BigDecimal(1).subtract(key))));
        //TODO Second
        BigDecimal ema1 =
                (stocks.get(0).getClose().multiply(key)
                        .add(new BigDecimal(1).subtract(key)))
                                .multiply(sma);


//        BigDecimal result = sum.divide(BigDecimal.valueOf(100));
        System.out.println("sma = " + sma);
        System.out.println("ema = " + ema);
        System.out.println("key = " + key);
//        System.out.println("sma = " + result);
    }
}
