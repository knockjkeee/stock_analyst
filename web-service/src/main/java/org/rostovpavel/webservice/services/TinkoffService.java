package org.rostovpavel.webservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.rostovpavel.webservice.exception.StockNotFoundException;
import org.rostovpavel.base.models.Stock;
import org.rostovpavel.base.utils.DateFormatter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.contract.v1.Share;
import ru.tinkoff.piapi.core.InvestApi;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static ru.tinkoff.piapi.core.utils.MapperUtils.quotationToBigDecimal;


@Log4j2
@Service
@RequiredArgsConstructor
public class TinkoffService {
    private final InvestApi api = InvestApi.create(System.getenv("token"));

    public List<Stock> getListStockHistoricCandlesByFigi(String figi) {
        List<CompletableFuture<List<HistoricCandle>>> dataHistoryCandleByDate = getDataHistoryCandleByDate(figi);
        //TODO log getTradingStatusResponse
//        GetTradingStatusResponse getTradingStatusResponse = api.getMarketDataService().getTradingStatus(figi).join();
//        log.info("TradingStatus: " + getTradingStatusResponse.getTradingStatus().getNumber() + " " + getTradingStatusResponse.getTradingStatus().name());
        return dataHistoryCandleByDate
                .stream()
                .map(CompletableFuture::join)
                .filter(hs -> !hs.isEmpty())
                .map(histirycCandles -> {
                    List<Stock> stocks = histirycCandles
                            .stream()
                            .map(this::createStock).collect(Collectors.toList());
                    Collections.reverse(stocks);
                    log.info(DateFormatter.getTimeToString(stocks.get(0).getDate()) + "/" +
                                    DateFormatter.getTimeToString(stocks.get(stocks.size() - 1).getDate()));
                    return stocks;
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<CompletableFuture<List<HistoricCandle>>> getDataHistoryCandleByDate(String figi) {
        int[] dateConfig = DateFormatter.getCurrentDateConfig();
        Instant currentNow = Instant.now();
        List<CompletableFuture<List<HistoricCandle>>> result = new ArrayList<>();
        for (int i = 0; i < dateConfig.length; i = i + 2) {
            result.add(getDataHistoryCandleByDateConfig(figi, dateConfig, currentNow, i));
        }
        return result;
    }

    @Async
    public CompletableFuture<List<HistoricCandle>> getDataHistoryCandleByDateConfig(String figi, int[] dateConfig, Instant currentNow, int i) {
        return api.getMarketDataService().getCandles(figi,
                currentNow.minus(dateConfig[i], ChronoUnit.DAYS),
                currentNow.minus(dateConfig[i + 1], ChronoUnit.DAYS),
                CandleInterval.CANDLE_INTERVAL_15_MIN);
    }

    public String getCurrentFigi(String ticker, String classCode) {
        return api.getInstrumentsService()
                .getShareByTicker(ticker, classCode)
                .join()
                .orElseThrow(() -> new StockNotFoundException(ticker + " not found in InvestApi with " + classCode))
                .getFigi();
    }

    private Stock createStock(HistoricCandle candle) {
        return Stock.builder()
                .open(quotationToBigDecimal(candle.getOpen()))
                .high(quotationToBigDecimal(candle.getHigh()))
                .low(quotationToBigDecimal(candle.getLow()))
                .close(quotationToBigDecimal(candle.getClose()))
                .volume(candle.getVolume())
                .date(DateFormatter.getTimeStampToStringAtCurrentTimeZone(candle.getTime()))
                .build();
    }

    private static List<String> randomFigi(InvestApi api, int count) {
        return api.getInstrumentsService().getTradableSharesSync()
                .stream()
                .filter(el -> Boolean.TRUE.equals(el.getApiTradeAvailableFlag()))
                .map(Share::getFigi)
                .limit(count)
                .collect(Collectors.toList());
    }
}
