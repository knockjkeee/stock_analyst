package org.rostovpavel.webservice.services;

import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.rostovpavel.base.dto.StocksDTO;
import org.rostovpavel.base.dto.StocksDataDTO;
import org.rostovpavel.base.dto.TickersDTO;
import org.rostovpavel.base.models.Ticker;
import org.rostovpavel.base.models.TickerRequestBody;
import org.rostovpavel.base.models.move.AO.AwesomeOscillator;
import org.rostovpavel.base.models.move.BB.BollingerBands;
import org.rostovpavel.base.models.move.MA.MovingAverage;
import org.rostovpavel.base.models.move.MACD.MovingAverageConvergenceDivergence;
import org.rostovpavel.base.models.move.ST.SuperTrend;
import org.rostovpavel.base.models.power.ADX.AverageDirectionalMovementIndex;
import org.rostovpavel.base.models.power.ATR.AverageTrueRange;
import org.rostovpavel.base.models.purchases.CCI.CommodityChannel;
import org.rostovpavel.base.models.purchases.RSI.RelativeStrengthIndex;
import org.rostovpavel.base.models.purchases.RSI_SO.RelativeStrengthIndexStochastic;
import org.rostovpavel.base.repo.TickerRepo;
import org.rostovpavel.webservice.services.indicators.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private final ADXService adxService;
    private final AOService aoService;
    private final STService stService;
    private final TickerRepo repo;

    public Ticker getDataByTicker(@PathVariable String ticker) {
        StocksDTO stockDataByTicker = stockService.getStockDataByTicker(ticker);
        return generatedDataToTicker(ticker, stockDataByTicker, true);
    }

    public TickersDTO getDataByTickers(@RequestBody TickerRequestBody data){
        StocksDataDTO stockDataByTikers = stockService.getStockDataByTikers(data);
        List<Ticker> collect = stockDataByTikers.getStocks().stream().map(stockData -> generatedDataToTicker(stockData.getName(), stockData.getCandle(), false)).collect(Collectors.toList());
        return new TickersDTO(collect);
    }

    private Ticker generatedDataToTicker(String name, StocksDTO data,boolean isSingle) {
        BigDecimal price = data.getStocks().get(0).getClose().setScale(2, RoundingMode.HALF_UP);
        return generateTicker(name, data, price, isSingle);
    }

    @NotNull
    private Ticker generateTicker(String name, StocksDTO data, BigDecimal price, boolean isSingle) {
        Ticker ticker = prepareTicker(name, data, price);

        List<Ticker> tickersByRepo = repo.findByNameOrderByIdDesc(name);
        if (tickersByRepo.size() >= 3) {
            prepareHistoryPoint(price, ticker, tickersByRepo);
        }

        if (!isSingle) repo.save(ticker);
        return ticker;
    }

    @NotNull
    private Ticker prepareTicker(String name, StocksDTO data, BigDecimal price) {
        MovingAverage movingAverage = maService.getData(data);
        MovingAverageConvergenceDivergence macd = macdService.getData(data);
        BollingerBands bb = bbService.getData(data);
        //StochasticOscillator so = soService.getData(data);
        AwesomeOscillator ao = aoService.getData(data);

        RelativeStrengthIndex rsi = rsiService.getData(data);
        RelativeStrengthIndexStochastic stochRSI = rsiStochService.getData(data);
        CommodityChannel cci = cciService.getData(data);
        AverageTrueRange atr = atrService.getData(data); //first atr!!!
        AverageDirectionalMovementIndex adx = adxService.getData(data);

        SuperTrend st = stService.getData(data);
        Ticker ticker = Ticker.builder()
                .name(name)
                .price(price)
                .candle(data.getStocks().size())
                .movingAverage(movingAverage)
                .macd(macd)
                .bollingerBands(bb)
                //.stochasticOscillator(so)
                .awesomeOscillator(ao)
                .superTrend(st)
                .rsi(rsi)
                .stochRSI(stochRSI)
                .cci(cci)
                .atr(atr)
                .adx(adx)
                .time(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()))
                .build();
        ticker.generateScoreIndicators();
        return ticker;
    }

    private void prepareHistoryPoint(BigDecimal price, Ticker ticker, List<Ticker> tickersByRepo) {
        List<BigDecimal> hPrice = tickersByRepo.stream().limit(3).map(Ticker::getPrice).collect(Collectors.toList());
        List<Integer> hPriceDiff = getHistoryDiffFromCorrectData(hPrice, price);
        int hPriceDiffSum = hPriceDiff.stream().mapToInt(i -> i).sum();
        ticker.setHPrice(hPriceDiffSum);

        List<BigDecimal> hMACDWidth = tickersByRepo.stream().limit(3).map(e -> e.getMacd().getWidthLine()).collect(Collectors.toList());
        List<Integer> hMACDWidthDiff = getHistoryDiffFromCorrectData(hMACDWidth, price);
        int hMACDWidthDiffSum = hMACDWidthDiff .stream().mapToInt(i -> i).sum();
        ticker.setHMACD(hMACDWidthDiffSum);

        List<BigDecimal> hBBWidth = tickersByRepo.stream().limit(3).map(e -> e.getBollingerBands().getWidthBand()).collect(Collectors.toList());
        List<Integer> hBBWidthDiff = getHistoryDiffFromCorrectData(hBBWidth, price);
        int hBBWidthSum = hBBWidthDiff .stream().mapToInt(i -> i).sum();
        ticker.setHBB(hBBWidthSum);

        List<BigDecimal> hAO = tickersByRepo.stream().limit(3).map(e -> e.getAwesomeOscillator().getAo()).collect(Collectors.toList());
        List<Integer> hAODiff = getHistoryDiffFromCorrectData(hAO, price);
        int hAODiffSum = hAODiff .stream().mapToInt(i -> i).sum();
        ticker.setHAO(hAODiffSum);
    }

    private List<Integer> getHistoryDiffFromCorrectData(List<BigDecimal> data, BigDecimal price) {
        List<Integer> res = new ArrayList<>();
        res.add(getPint(data.get(0), price));
        res.add(getPint(data.get(1), data.get(0)));
        res.add(getPint(data.get(2), data.get(1)));
        return res;
    }

    private int getPint(BigDecimal B, BigDecimal A) {
        return Integer.compare(A.compareTo(B), 0);
    }
}
