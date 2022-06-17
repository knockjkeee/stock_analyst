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
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import static org.rostovpavel.base.utils.ArraysFormat.generateHistorySum;
import static org.rostovpavel.base.utils.Math.calculateGrowthAsPercentage;

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

    public TickersDTO getDataByTickers(@RequestBody TickerRequestBody data) {
        StocksDataDTO stockDataByTikers = stockService.getStockDataByTikers(data);
        List<Ticker> collect = stockDataByTikers.getStocks()
                .stream()
                .filter(e -> e.getCandle().getStocks().size() > 0)
                .map(stockData -> generatedDataToTicker(stockData.getName(), stockData.getCandle(), false))
                .collect(Collectors.toList());
        return new TickersDTO(collect);
    }

    private Ticker generatedDataToTicker(String name, StocksDTO data, boolean isSingle) {
        BigDecimal price = data.getStocks().get(0).getClose().setScale(2, RoundingMode.HALF_UP);
        return generateTicker(name, data, price, isSingle);
    }

    @NotNull
    private Ticker generateTicker(String name, StocksDTO data, BigDecimal price, boolean isSingle) {
        Ticker ticker = prepareTicker(name, data, price);
        List<Ticker> tickersByRepo = repo.findByNameOrderByIdDesc(name);
        if (tickersByRepo.size() >= 3) prepareHistoryPoint(price, ticker, tickersByRepo);
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
        prepareHistoryPointPrice(price, ticker, tickersByRepo);
        prepareHistoryPointPriceMAD(ticker, tickersByRepo);
        prepareHistoryPointPriceAO(ticker, tickersByRepo);
        prepareHistoryPointPriceBB(ticker, tickersByRepo);
    }

    private void prepareHistoryPointPrice(BigDecimal price, Ticker ticker, List<Ticker> tickersByRepo) {
        //TODO Price
        List<BigDecimal> hPrice = tickersByRepo.stream().limit(3).map(Ticker::getPrice).collect(Collectors.toList());
        int hPriceDiffSum = generateHistorySum(hPrice, price);
        ticker.setHPrice(hPriceDiffSum);
    }

    private void prepareHistoryPointPriceMAD(Ticker ticker, List<Ticker> tickersByRepo) {
        //TODO MACD
        List<BigDecimal> hMACDWidth = tickersByRepo.stream()
                .limit(3)
                .map(e -> e.getMacd().getWidthLine())
                .collect(Collectors.toList());
        int hMACDWidthDiffSum = generateHistorySum(hMACDWidth, ticker.getMacd().getWidthLine());
        ticker.setHMACD(hMACDWidthDiffSum);

        List<BigDecimal> hMACDHistogram = tickersByRepo.stream()
                .limit(3)
                .map(e -> e.getMacd().getHistogram())
                .collect(Collectors.toList());
        int hMACDHistogramDiffSum = generateHistorySum(hMACDHistogram, ticker.getMacd().getHistogram(), true);
        ticker.setHMACDHistogram(hMACDHistogramDiffSum);

        List<BigDecimal> hMACDProcent = tickersByRepo.stream()
                .limit(3)
                .map(e -> e.getMacd().getProcent())
                .collect(Collectors.toList());
        try {
            ticker.setHMACDProcent(hMACDProcent.get(1).setScale(2, RoundingMode.HALF_UP) + "/" + hMACDProcent.get(0)
                    .setScale(2, RoundingMode.HALF_UP) + "/" + ticker.getMacd()
                    .getProcent()
                    .setScale(2, RoundingMode.HALF_UP));
        } catch (Exception e) {
            log.error(ticker.getName() + ":: exception MACD Proc: " + e.getLocalizedMessage());
        }

        try {
            BigDecimal hMACDProcentResult02 = calculateGrowthAsPercentage(hMACDProcent.get(1), hMACDProcent.get(0));
            BigDecimal hMACDProcentResult12 = calculateGrowthAsPercentage(hMACDProcent.get(0), ticker.getMacd()
                    .getProcent());
            if (ticker.getMacd().getProcent().compareTo(BigDecimal.valueOf(0)) < 0
                    && hMACDProcent.get(1).compareTo(BigDecimal.valueOf(0)) > 0) {
                ticker.setHMACDProcentHis(hMACDProcentResult02.multiply(BigDecimal.valueOf(-1))
                        .setScale(2, RoundingMode.HALF_UP)
                        + "::"
                        + hMACDProcentResult12.multiply(BigDecimal.valueOf(-1)).setScale(2, RoundingMode.HALF_UP));
            } else if (ticker.getMacd().getProcent().compareTo(BigDecimal.valueOf(0)) > 0
                    && hMACDProcent.get(1).compareTo(BigDecimal.valueOf(0)) < 0) {
                ticker.setHMACDProcentHis(hMACDProcentResult02.setScale(2, RoundingMode.HALF_UP).abs()
                        + "::"
                        + hMACDProcentResult12.setScale(2, RoundingMode.HALF_UP).abs());
            } else {
                ticker.setHMACDProcentHis(hMACDProcentResult02.setScale(2, RoundingMode.HALF_UP)
                        + "::"
                        + hMACDProcentResult12.setScale(2, RoundingMode.HALF_UP));
            }
//             % = (B-A)/A*100
            ticker.setHMACDProcentResult(hMACDProcentResult12.subtract(hMACDProcentResult02));

        } catch (Exception e) {
            log.error(ticker.getName() + ":: exception MACD ProcRes: " + e.getLocalizedMessage());
        }
    }

    private void prepareHistoryPointPriceAO(Ticker ticker, List<Ticker> tickersByRepo) {
        //TODO AO
        List<BigDecimal> hAO = tickersByRepo.stream()
                .limit(3)
                .map(e -> e.getAwesomeOscillator().getAo())
                .collect(Collectors.toList());
        int hAODiffSum = generateHistorySum(hAO, ticker.getAwesomeOscillator().getAo());
        ticker.setHAO(hAODiffSum);

        List<String> hAODirection = tickersByRepo.stream()
                .limit(3)
                .map(e -> e.getAwesomeOscillator().getDirection())
                .collect(Collectors.toList());
        int hAODirectionDiffSum = generateHistorySum(hAODirection, ticker.getAwesomeOscillator().getDirection(), "Up");
        ticker.setHAODirection(hAODirectionDiffSum);

        List<String> hAOColor = tickersByRepo.stream()
                .limit(3)
                .map(e -> e.getAwesomeOscillator().getColor())
                .collect(Collectors.toList());
        int hAOColorDiffSum = generateHistorySum(hAOColor, ticker.getAwesomeOscillator().getColor(), "Green");
        ticker.setHAOColor(hAOColorDiffSum);
    }

    private void prepareHistoryPointPriceBB(Ticker ticker, List<Ticker> tickersByRepo) {
        //TODO BB
        List<BigDecimal> hBBWidth = tickersByRepo.stream()
                .limit(3)
                .map(e -> e.getBollingerBands().getWidthBand())
                .collect(Collectors.toList());
        int hBBWidthSum = generateHistorySum(hBBWidth, ticker.getBollingerBands().getWidthBand());
        ticker.setHBB(hBBWidthSum);
    }
}
