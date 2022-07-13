package org.rostovpavel.webservice.controller;

import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.rostovpavel.base.dto.TickersDTO;
import org.rostovpavel.base.models.Ticker;
import org.rostovpavel.base.models.TickerRequestBody;
import org.rostovpavel.base.models.state.DirectionState;
import org.rostovpavel.base.models.state.PowerState;
import org.rostovpavel.webservice.TEMPO.GenerateFile;
import org.rostovpavel.webservice.services.TickerDataService;
import org.rostovpavel.webservice.telegram.StockBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.rostovpavel.base.utils.Stock.getNameTickers;

@RestController
@RequestMapping("/v1/api")
public class Endpoint {

    private final TickerDataService tickerDataService;
    private final StockBot stockBot;

    @Value("${telegram.idChat}")
    private String idChat;


    public Endpoint(TickerDataService tickerDataService, StockBot stockBot) {
        this.tickerDataService = tickerDataService;
        this.stockBot = stockBot;
    }

    @Contract(" -> new")
    @GetMapping("/csv")
    public @NotNull ResponseEntity<String> generateCSV() {
        GenerateFile.generateCSV("All_stocks");
        GenerateFile.generateCSV("history");

        return new ResponseEntity<>("Success filter and All_stocks", HttpStatus.OK);
    }

    @Contract(" -> new")
    @GetMapping("/val")
    public @NotNull TickersDTO getFilterVal() {
        List<String> date = Arrays.asList(getNameTickers());
        TickerRequestBody tickerRequestBody = new TickerRequestBody();
        tickerRequestBody.setTickers(date);
        List<Ticker> collect = tickerDataService.getDataByTickers(tickerRequestBody)
                .getStocks()
                .stream()
                .filter(e -> e.getScorePowerVal() != 0)
                .collect(Collectors.toList());
        return new TickersDTO(collect);
    }

    @Contract(" -> new")
    @GetMapping("/trend")
    public @NotNull TickersDTO getFilterTrend() {
        List<String> date = Arrays.asList(getNameTickers());
        TickerRequestBody tickerRequestBody = new TickerRequestBody();
        tickerRequestBody.setTickers(date);
        List<Ticker> collect = tickerDataService.getDataByTickers(tickerRequestBody)
                .getStocks()
                .stream()
                .filter(e -> e.getScorePowerTrend() > 0)
                .collect(Collectors.toList());
        return new TickersDTO(collect);
    }

    @Contract(" -> new")
    @GetMapping("/pur")
    public @NotNull TickersDTO getFilterPurchases() {
        List<String> date = Arrays.asList(getNameTickers());
        TickerRequestBody tickerRequestBody = new TickerRequestBody();
        tickerRequestBody.setTickers(date);
        List<Ticker> collect = tickerDataService.getDataByTickers(tickerRequestBody)
                .getStocks()
                .stream()
                .filter(e -> e.getScorePurchases() < -50 || e.getScorePurchases() > 50)
                .collect(Collectors.toList());
        return new TickersDTO(collect);
    }

    @PostMapping("/data")
    public TickersDTO getDataByTickers(@RequestBody TickerRequestBody data) {
        return tickerDataService.getDataByTickers(data);
    }

    @GetMapping("/all")
    public TickersDTO getTest() {
        List<String> date = Arrays.asList(getNameTickers());
        TickerRequestBody tickerRequestBody = new TickerRequestBody();
        tickerRequestBody.setTickers(date);
        return tickerDataService.getDataByTickers(tickerRequestBody);
    }

    @Contract(" -> new")
    @GetMapping("/move")
    public @NotNull TickersDTO gatFilterMove() {
        List<String> date = Arrays.asList(getNameTickers());
        TickerRequestBody tickerRequestBody = new TickerRequestBody();
        tickerRequestBody.setTickers(date);
        List<Ticker> collect = tickerDataService.getDataByTickers(tickerRequestBody)
                .getStocks()
                .stream()
                .filter(e -> e.getScoreMove() > 120 || e.getScoreMove() < -120)
                .collect(Collectors.toList());
        return new TickersDTO(collect);
    }

    @Contract(" -> new")
    @GetMapping("/up")
    public @NotNull TickersDTO gatFilterUp() {
        List<String> date = Arrays.asList(getNameTickers());
        TickerRequestBody tickerRequestBody = new TickerRequestBody();
        tickerRequestBody.setTickers(date);
        //all
        List<Ticker> stocks = tickerDataService.getDataByTickers(tickerRequestBody)
                .getStocks();
        //write all
        GenerateFile.writeToJson(stocks, "All_stocks");

        List<Ticker> history = stocks.stream()
                .filter(e ->
                        ( ((e.getSuperTrend().get_keyMain().equals("BUY++") || e.getSuperTrend().get_keyMain().equals("SELL--"))
                            && (e.getHMACDProcentHis().contains("+Up") || e.getHMACDProcentHis().contains("-Up"))
                            && e.getScorePowerVal() > 0)
                            || ((e.getHMACD() == 3 && e.getHMACDHistogram() == 3 && e.getHPrice() == 3 && e.getHAO() >= 2 &&
                                e.getHAODirection() > 1)
                                || (e.getHMACD() == -3 && e.getHMACDHistogram() == -3 && e.getHPrice() == -3 && e.getHAO() <= -2 && e.getHAODirection() < 1)
                                ||
                                (e.getHPrice() >= 1 && e.getHMACD() == 3 && e.getHMACDHistogram() > 1 &&
                                        e.getHAO() == 3 && e.getHAODirection() > 1)))
                        && e.getHMACDProcentResult().compareTo(BigDecimal.valueOf(0)) > 0)
                .collect(Collectors.toList());

        GenerateFile.writeToJson(history, "history");

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
        return new TickersDTO(history);
    }

    @SneakyThrows
    @GetMapping("{ticker}")
    public Ticker getDataByTicker(@PathVariable String ticker) {
        Ticker ticket = tickerDataService.getDataByTicker(ticker);
        stockBot.execute(SendMessage.builder()
                .chatId(idChat)
                .text(getNamedByTicket(ticket))
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .build());
        return ticket;
    }


    public String getNamedByTicket(Ticker ticker) {
        String hrefName =
                "<a href=\"www.tinkoff.ru/invest/stocks/" + ticker.getName() + "\">" + ticker.getName() + "</a>";

        String result = hrefName + "\t\t" + ticker.getPrice() + "(<b>" + ticker.getHPrice() + "</b>)" + "\t\t" +
                DirectionState.getState(ticker.getHPrice() > 0)
                        .getLabel() + "\t\t" + ticker.getSuperTrend()
                .get_keyMain() + "/" + ticker.getSuperTrend()
                .get_keySecond() +
//                "\n<pre>(350)" + " 350 / 350 / 350 / 350</pre>";
                "\n<pre>(" + ticker.getMovingAverage()
                .getInnerScore() + ") " + ticker.getScoreMove() + " / " + ticker.getScorePowerVal() + " / " +
                ticker.getScorePowerTrend() + " / " + ticker.getScorePurchases() + "</pre>" + "\n(" +
                ticker.getHMACD() + "/" + ticker.getHMACDHistogram() + ") " + "\t\t\t" + ticker.getHMACDProcent()  +
                "\n<pre>(" + ticker.getHMACDProcentResult() + ") " + ticker.getHMACDProcentHis() + "</pre>" +
                "\n" + "(" + ticker.getHBB() + ") " + "\t\t\t\t" + ticker.getHAO() + " / " + ticker.getHAODirection() +
                " / " + ticker.getHAOColor() + "\t\t" + PowerState.getState(ticker.getHMACDProcentResult()
                        .abs()
                        .intValue() > 5)
                .getLabel();

        return result;
    }


//    public String getNamedByTicket(Ticker ticker) {
//        String res = "\t*" + ticker.getName() + "* \t\t " + ticker.getPrice() + " \t\t " + ticker.getSuperTrend()
//              ~ .get_keyMain() + "/" + ticker.getSuperTrend()
//              ~  .get_keySecond() + " \t\t " + ticker.getTime() + "\n*Mov/Val/Tre/Pur/MA*" + " \t\t " +
//              ~  ticker.getScoreMove() + "/" + ticker.getScorePowerVal() + "/" + ticker.getScorePowerTrend() + "/" +
//              ~  ticker.getScorePurchases() + "/" + ticker.getMovingAverage()
//              ~  .getInnerScore() + "\n hPrice/hMACD/hMACDHis" + " \t\t " + ticker.getHPrice() + "/" +
//                ticker.getHMACD() + "/" + ticker.getHMACDHistogram() + "\n" + ticker.getHMACDProcent() + "\n" +
//                ticker.getHMACDProcentHis() + "\n" + ticker.getHMACDProcentResult() + "\nhAO/hAODir/hAOCol/hBB" +
//                " \t\t " + ticker.getHAO() + "/" + ticker.getHAODirection() + "/" + ticker.getHAOColor() + "/" +
//                ticker.getHBB();
//
//        return res;
//    }
}
