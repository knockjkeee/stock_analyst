package org.rostovpavel.base.utils;

import org.jetbrains.annotations.NotNull;
import org.rostovpavel.base.models.Ticker;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class TickerUtils {

    @NotNull
    public static List<Ticker> getHistory(List<Ticker> stocks) {
        return stocks.stream()
                .filter(e ->
                        (((e.getHMACD() == 3 && e.getHMACDHistogram() == 3 && e.getHPrice() == 3 && e.getHAO() >= 2 &&
                                e.getHAODirection() > 1) ||
                                (e.getHMACD() == -3 && e.getHMACDHistogram() == -3 && e.getHPrice() == -3 &&
                                        e.getHAO() <= -2 && e.getHAODirection() < 1) ||
                                (e.getHPrice() >= 1 && e.getHMACD() == 3 && e.getHMACDHistogram() > 1 &&
                                        e.getHAO() == 3 && e.getHAODirection() > 1))) && e.getHMACDProcentResult()
                                .compareTo(BigDecimal.valueOf(2)) > 0)
                .peek(e -> e.setGroupId(2))
                .collect(Collectors.toList());
    }
}
