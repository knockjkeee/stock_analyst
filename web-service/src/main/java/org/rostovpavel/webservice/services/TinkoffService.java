package org.rostovpavel.webservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.rostovpavel.base.models.Stock;
import org.rostovpavel.base.models.StockDTO;
import org.rostovpavel.webservice.exception.StockNotFoundException;
import org.rostovpavel.webservice.utils.DateTimeFormatter;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.InvestApi;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static ru.tinkoff.piapi.core.utils.MapperUtils.quotationToBigDecimal;

@Log4j2
@Service
@RequiredArgsConstructor
public class TinkoffService {
    private final InvestApi  api = InvestApi.create(System.getenv("token"));

    public StockDTO getCandles(String ticker) {
        String figi = getCurrentFigi(ticker, "SPBXM");
        List<HistoricCandle> currentHistoricCandle = getHistoricCandlesByFigi(figi);
        List<Stock> stocks = currentHistoricCandle.stream().map(historicCandle ->
                Stock.builder()
                        .open(quotationToBigDecimal(historicCandle.getOpen()))
                        .close(quotationToBigDecimal(historicCandle.getClose()))
                        .high(quotationToBigDecimal(historicCandle.getHigh()))
                        .low(quotationToBigDecimal(historicCandle.getLow()))
                        .volume(historicCandle.getVolume())
                        .date(DateTimeFormatter.getTimeStampToStringAtCurrentTimeZone(historicCandle.getTime()))
                        .build())
                .collect(Collectors.toList());
        return new StockDTO(stocks);
    }

    private List<HistoricCandle> getHistoricCandlesByFigi(String figi) {
        int[] dateConfig = DateTimeFormatter.getCurrentDateConfig();
        Instant currentNow = Instant.now();
        List<HistoricCandle> result = new ArrayList<>();

        for (int i = 0; i < dateConfig.length; i = i + 2) {
            List<HistoricCandle> collect = new ArrayList<>(api.getMarketDataService().getCandles(figi,
                            currentNow.minus(dateConfig[i], ChronoUnit.DAYS),
                            currentNow.minus(dateConfig[i + 1], ChronoUnit.DAYS),
                            CandleInterval.CANDLE_INTERVAL_15_MIN)
                    .join());
            if (collect.isEmpty()) {
                throw new StockNotFoundException(String.format("Collection by day %s-%s with interval %s is empty",
                        currentNow.minus(dateConfig[i], ChronoUnit.DAYS),
                        currentNow.minus(dateConfig[i + 1], ChronoUnit.DAYS),
                        CandleInterval.CANDLE_INTERVAL_15_MIN));
            }
            Collections.reverse(collect);
            result.addAll(collect);
        }
        return result;
    }

    private String getCurrentFigi(String ticker, String classCode) {
        return api.getInstrumentsService()
                .getShareByTicker(ticker, classCode)
                .join()
                .orElseThrow( () -> new StockNotFoundException(ticker + " not found in InvestApi with " + classCode))
                .getFigi();
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
