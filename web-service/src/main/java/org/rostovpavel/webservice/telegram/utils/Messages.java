package org.rostovpavel.webservice.telegram.utils;

import org.jetbrains.annotations.NotNull;
import org.rostovpavel.base.models.Ticker;
import org.rostovpavel.base.models.state.DirectionState;
import org.rostovpavel.base.models.state.GroupState;
import org.rostovpavel.base.models.state.PowerState;
import org.rostovpavel.webservice.telegram.StockBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class Messages {

    public static String getNamedByTicket(Ticker ticker) {
        String hrefName =
                "<a href=\"www.tinkoff.ru/invest/stocks/" + ticker.getName() + "\">" + ticker.getName() + "</a>";

        return hrefName + "\t\t" + ticker.getPrice() + " (<b>" + ticker.getHPrice() + "</b>)" + "\t\t" +
                GroupState.getState(ticker.getGroupId()).getLabel() + DirectionState.getState(ticker.getHPrice())
                .getLabel() + "\t\t" + ticker.getSuperTrend()
                .get_keyMain() + "/" + ticker.getSuperTrend()
                .get_keySecond() + "\n<pre>(" + ticker.getMovingAverage()
                .getInnerScore() + ") " + ticker.getScoreMove() + " / " + ticker.getScorePowerVal() + " / " +
                ticker.getScorePowerTrend() + " / " + ticker.getScorePurchases() + "</pre>" + "\n(" +
                ticker.getHMACD() + "/" + ticker.getHMACDHistogram() + ") " + "\t\t\t" + ticker.getHMACDProcent() +
                "\n<pre>(" + ticker.getHMACDProcentResult() + ") " + ticker.getHMACDProcentHis() + "</pre>" + "\n" +
                "(" + ticker.getHBB() + ") " + "\t\t\t\t" + ticker.getHAO() + " / " + ticker.getHAODirection() + " / " +
                ticker.getHAOColor() + "\t\t" + PowerState.getState(ticker.getHMACDProcentResult() != null &&
                        ticker.getHMACDProcentResult().abs().intValue() > 5)
                .getLabel();
    }

    @NotNull
    public static List<Ticker> getSuperTrend(List<Ticker> stocks) {
        return stocks.stream()
                .filter(e -> (((e.getSuperTrend()
                        .get_keyMain()
                        .equals("BUY++") || e.getSuperTrend()
                        .get_keyMain()
                        .equals("SELL--")) && (e.getHMACDProcentHis()
                        .contains("+Up") || e.getHMACDProcentHis()
                        .contains("-Up")) && e.getScorePowerVal() > 0)) && e.getHMACDProcentResult()
                        .compareTo(BigDecimal.valueOf(0)) > 0)
                .peek(e -> e.setGroupId(1))
                .collect(Collectors.toList());
    }

    public static void sendExceptionToBot(Exception ex, StockBot bot, String idChat) throws TelegramApiException {
        bot.execute(SendMessage.builder()
                .chatId(idChat)
                .text("<pre>"+ ex.getLocalizedMessage()+"</pre>")
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .build());
    }

    public static void sendDataToBot(List<Ticker> history, StockBot stockBot, String idChat ) {
        history.forEach(e -> {
            try {
                stockBot.execute(SendMessage.builder()
                        .chatId(idChat)
                        .text(getNamedByTicket(e))
                        .parseMode(ParseMode.HTML)
                        .disableWebPagePreview(true)
                        .build());
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        });
    }
}
