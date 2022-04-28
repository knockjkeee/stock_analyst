package org.rostovpavel.webservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.rostovpavel.base.dto.StocksDTO;
import org.rostovpavel.base.dto.StocksDataDTO;
import org.rostovpavel.base.dto.TickersDTO;
import org.rostovpavel.base.models.CCI.CommodityChannel;
import org.rostovpavel.base.models.MA.MovingAverage;
import org.rostovpavel.base.models.RSI.RelativeStrengthIndex;
import org.rostovpavel.base.models.RSI_Stochastic.RelativeStrengthIndexStochastic;
import org.rostovpavel.base.models.Ticker;
import org.rostovpavel.base.models.TickerRequestBody;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class TickerDataService {
    private final StockService stockService;
    private final RSIService rsiService;
    private final MAService maService;
    private final CCIService cciService;
    private final RSIStochService rsiStochService;

    public Ticker getDataByTicker(@PathVariable String ticker) {
        StocksDTO stockDataByTicker = stockService.getStockDataByTicker(ticker);
        return generatedDataToTicker(ticker, stockDataByTicker);
    }


    public TickersDTO getDataByTickers(@RequestBody TickerRequestBody data){

        StocksDataDTO stockDataByTikers = stockService.getStockDataByTikers(data);

        List<Ticker> collect = stockDataByTikers.getStocks().stream().map(stockData -> {
            return generatedDataToTicker(stockData.getName(), stockData.getCandle());
        }).collect(Collectors.toList());

        return new TickersDTO(collect);
    }

    private Ticker generatedDataToTicker(String ticker, StocksDTO stockDataByTicker) {
        BigDecimal price = stockDataByTicker.getStocks().get(0).getClose().setScale(2, RoundingMode.HALF_UP);
        MovingAverage movingAverage = maService.getMovingAverage(stockDataByTicker);
        CommodityChannel cci = cciService.getCCI(stockDataByTicker);
        RelativeStrengthIndex rsi = rsiService.getRSI(0, stockDataByTicker);
        RelativeStrengthIndexStochastic stochRSI = rsiStochService.getStochRSI(stockDataByTicker);
        //revers
        return Ticker.builder()
                .name(ticker)
                .price(price)
                .ma(movingAverage)
                .cci(cci)
                .rsi(rsi)
                .stochRSI(stochRSI)
                .build();
    }

}
