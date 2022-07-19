package org.rostovpavel.webservice.telegram.utils;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.rostovpavel.base.models.Ticker;
import org.rostovpavel.base.models.state.DirectionState;
import org.rostovpavel.base.models.state.GroupState;
import org.rostovpavel.base.models.state.PowerState;
import org.rostovpavel.base.repo.TickerRepo;
import org.rostovpavel.webservice.telegram.StockBot;
import org.rostovpavel.webservice.telegram.query.dto.IndicatorDTO;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Messages {

    public static String getNamedByTicket(Ticker ticker) {
        String hrefName =
                "<a href=\"www.tinkoff.ru/invest/stocks/" + ticker.getName() + "\">" + ticker.getName() + "</a>";

        return hrefName + "\t\t" + ticker.getPrice() + " (<b>" + ticker.getHPrice() + "</b>)" + "\t\t" +
                GroupState.getState(ticker.getGroupId()).getLabel() +
                DirectionState.getState(ticker.getHPrice()).getLabel() + "\t\t" + ticker.getSuperTrend().get_keyMain() +
                "/" + ticker.getSuperTrend().get_keySecond() +
                //score
                "\n<pre>(" + ticker.getMovingAverage().getInnerScore() +
                ") " + ticker.getScoreMove() + " / " + ticker.getScorePowerVal() + " / " + ticker.getScorePowerTrend() +
                " / " + ticker.getScorePurchases() + "</pre>" +
                //Hmacd
                "\n(" + ticker.getHMACD() + "/" +
                ticker.getHMACDHistogram() + ") " + "\t\t\t" + ticker.getHMACDProcent() +
                //hmacdProcent
                "\n<pre>(" +
                ticker.getHMACDProcentResult() + ") " + ticker.getHMACDProcentHis() + "</pre>" +
                //hAO
                "\n" + "(" +
                ticker.getHBB() + ") " + (ticker.getHBB() == 1 || ticker.getHBB() == -1 ? "❗❗❗" : "") + "\t\t\t\t" +
                ticker.getHAO() + " / " + ticker.getHAODirection() + " / " + ticker.getHAOColor() + "\t\t" +
                PowerState.getState(
                                ticker.getHMACDProcentResult() != null && ticker.getHMACDProcentResult().abs().intValue() > 5)
                        .getLabel();
    }

    public static String getIndicatorByTicket(String text, Ticker ticker) {
        return text +
                //sma
                "\n\n<pre>Moving Average (" + ticker.getMovingAverage().getInnerScore() + ")</pre>\n" +
                ticker.getMovingAverage().getSma().graphicItem(ticker.getPrice())
                //bb
                + "\n<pre>BollingerBands (" +
                (ticker.getBollingerBands().getScoreToLine() + ticker.getBollingerBands().getScoreToKeys() +
                        ticker.getBollingerBands().getScoreToSignal()) + ")</pre>\n" +
                ticker.getBollingerBands().graphicItem(ticker.getPrice())
                //macd
                + "\n<pre>MACD (" + (ticker.getMacd().getScoreToLine() + ticker.getMacd().getScoreToKeys() +
                ticker.getMacd().getScoreToSignal()) + ")</pre>\n" + ticker.getMacd().graphicItem()
                //ao
                + "\n<pre>AwesomeOscillator (" +
                (ticker.getAwesomeOscillator().getScoreLine() + ticker.getAwesomeOscillator().getScoreKey() +
                        ticker.getAwesomeOscillator().getScoreSignal()) + ")</pre>\n" +
                ticker.getAwesomeOscillator().graphicItem()
                //st
                + "\n<pre>SuperTrend (" + ticker.getSuperTrend().getScoreKey() + ")</pre>\n" +
                ticker.getSuperTrend().graphicItem();
    }

    public static String getFullInformationByTicker(Ticker ticker) {
        String firstStep = getNamedByTicket(ticker);
        return getIndicatorByTicket(firstStep, ticker);
    }

    @NotNull
    public static List<Ticker> getSuperTrend(List<Ticker> stocks, TickerRepo repo) {
        return stocks.stream()
//                .filter(e -> e.getHMACDProcentHis() != null)
                .filter(e -> (((e.getSuperTrend().get_keyMain().equals("BUY++") ||
                        e.getSuperTrend().get_keyMain().equals("SELL--")) &&
                        (e.getHMACDProcentHis().contains("+Up") || e.getHMACDProcentHis().contains("-Up")) &&
                        e.getScorePowerVal() > 0)) && e.getHMACDProcentResult().compareTo(BigDecimal.valueOf(0)) > 0)
                .peek(e -> {
                    e.setGroupId(1);
                    repo.save(e);
                })
                .collect(Collectors.toList());
    }

    public static void sendExceptionToBot(Exception ex, StockBot bot, String idChat) throws TelegramApiException {
        bot.execute(SendMessage.builder()
                .chatId(idChat)
                .text("<pre>" + ex.getLocalizedMessage() + "</pre>")
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .build());
    }

    @SneakyThrows
    public static void sendDataToBot(List<Ticker> data, StockBot stockBot, String idChat) {

        int sizeGroupTwo = (int) data.stream().filter(e -> e.getGroupId() == 2).count();
        stockBot.execute(SendMessage.builder()
                .chatId(idChat)
                .text("<pre>New Candle " + "(" + data.size() + " / " + sizeGroupTwo + " / " +
                        (data.size() - sizeGroupTwo) + ") time: " + data.get(0).getTime() + " </pre>")
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .build());

        data.forEach(e -> {
            try {
                sendData(stockBot, idChat, e);
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        });
    }

    public static void sendData(StockBot stockBot, String idChat, Ticker ticker) throws TelegramApiException {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(List.of(InlineKeyboardButton.builder()
                .text("Индикаторы")
                .callbackData(StringUtil.serialize(new IndicatorDTO(ticker.getId())))
                .build()));
        stockBot.execute(SendMessage.builder()
                .chatId(idChat)
                .text(getNamedByTicket(ticker))
                .parseMode(ParseMode.HTML)
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .disableWebPagePreview(true)
                .build());
    }
}
