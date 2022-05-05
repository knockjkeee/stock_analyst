package org.rostovpavel.webservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.rostovpavel.base.dto.StocksDTO;
import org.rostovpavel.base.dto.StocksDataDTO;
import org.rostovpavel.base.dto.TickersDTO;
import org.rostovpavel.base.models.ATR.AverageTrueRange;
import org.rostovpavel.base.models.BB.BollingerBands;
import org.rostovpavel.base.models.CCI.CommodityChannel;
import org.rostovpavel.base.models.MA.MovingAverage;
import org.rostovpavel.base.models.MACD.MovingAverageConvergenceDivergence;
import org.rostovpavel.base.models.RSI.RelativeStrengthIndex;
import org.rostovpavel.base.models.RSI_SO.RelativeStrengthIndexStochastic;
import org.rostovpavel.base.models.SO.StochasticOscillator;
import org.rostovpavel.base.models.Ticker;
import org.rostovpavel.base.models.TickerRequestBody;
import org.rostovpavel.webservice.services.indicators.*;
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
    private final SOService soService;
    private final MACDService macdService;
    private final BBService bbService;
    private final ATRService atrService;

    public Ticker getDataByTicker(@PathVariable String ticker) {
        StocksDTO stockDataByTicker = stockService.getStockDataByTicker(ticker);
        return generatedDataToTicker(ticker, stockDataByTicker);
    }


    public TickersDTO getDataByTickers(@RequestBody TickerRequestBody data){
        StocksDataDTO stockDataByTikers = stockService.getStockDataByTikers(data);
        List<Ticker> collect = stockDataByTikers.getStocks().stream().map(stockData -> generatedDataToTicker(stockData.getName(), stockData.getCandle())).collect(Collectors.toList());
        return new TickersDTO(collect);
    }

    private Ticker generatedDataToTicker(String ticker, StocksDTO data) {
        BigDecimal price = data.getStocks().get(0).getClose().setScale(2, RoundingMode.HALF_UP);
        MovingAverage movingAverage = maService.getData(data);
        CommodityChannel cci = cciService.getData(data);
        StochasticOscillator so = soService.getData(data);
        RelativeStrengthIndex rsi = rsiService.getData(data);
        RelativeStrengthIndexStochastic stochRSI = rsiStochService.getData(data);
        MovingAverageConvergenceDivergence macd = macdService.getData(data);
        BollingerBands bb = bbService.getData(data);
        AverageTrueRange atr = atrService.getData(data);

        return Ticker.builder()
                .name(ticker)
                .price(price)
                .candle(data.getStocks().size())
                .movingAverage(movingAverage)
                .rsi(rsi)
                .stochRSI(stochRSI)
                .cci(cci)
                .stochasticOscillator(so)
                .macd(macd)
                .bollingerBands(bb)
                .atr(atr)
                .build();
    }

}
