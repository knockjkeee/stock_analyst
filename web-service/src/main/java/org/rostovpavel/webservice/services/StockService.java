package org.rostovpavel.webservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.rostovpavel.base.dto.StocksDTO;
import org.rostovpavel.base.dto.StocksDataDTO;
import org.rostovpavel.base.models.Stock;
import org.rostovpavel.base.models.StockData;
import org.rostovpavel.base.models.TickerRequestBody;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class StockService {
    //private final InvestApi api = InvestApi.create(System.getenv("token"));
    private final TinkoffService tinkoffService;

    public StocksDTO getStockDataByTicker(String ticker) {
        String figi = tinkoffService.getCurrentFigi(ticker, "SPBXM");
        List<Stock> currentHistoricCandle = tinkoffService.getListStockHistoricCandlesByFigi(figi);
        return new StocksDTO(currentHistoricCandle);
    }

    public StocksDataDTO getStockDataByTikers(TickerRequestBody data){
        List<StockData> date = data.getTickers().stream()
                .map(ticker -> {
                    String figi = tinkoffService.getCurrentFigi(ticker, "SPBXM");
                    List<Stock> candle = tinkoffService.getListStockHistoricCandlesByFigi(figi);
                    return StockData.builder()
                            .name(ticker)
                            .candle(new StocksDTO(candle))
                            .build();
                }).collect(Collectors.toList());
        return new StocksDataDTO(date);
    }
}
