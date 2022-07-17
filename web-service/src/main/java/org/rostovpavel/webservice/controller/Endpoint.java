package org.rostovpavel.webservice.controller;

import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.rostovpavel.base.dto.TickersDTO;
import org.rostovpavel.base.models.Ticker;
import org.rostovpavel.base.models.TickerRequestBody;
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

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.rostovpavel.base.utils.Stock.getNameTickers;
import static org.rostovpavel.base.utils.TickerUtils.getHistory;
import static org.rostovpavel.webservice.telegram.utils.Messages.*;

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
        GenerateFile.generateCSV("superTrend");

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
        GenerateFile.writeToJson(stocks, "All_stocks");

        List<Ticker> superTrend = getSuperTrend(stocks);
        GenerateFile.writeToJson(superTrend, "superTrend");

        List<Ticker> history = getHistory(stocks);
        GenerateFile.writeToJson(history, "history");

        history.addAll(superTrend);
        List<Ticker> collect = history.stream()
                .filter(e -> !superTrend.contains(e))
                .collect(Collectors.toList());
        collect.addAll(superTrend);

        if (collect.size() > 0) {
            sendDataToBot(collect, stockBot, idChat);
        } else {
            try {
                sendData(stockBot, idChat, stocks.get(new Random().nextInt(stocks.size() -1)));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        return new TickersDTO(collect);
    }

    @SneakyThrows
    @GetMapping("{ticker}")
    public Ticker getDataByTicker(@PathVariable String ticker) {
        Ticker ticket = tickerDataService.getDataByTicker(ticker);
        String text = getFullInformationByTicker(ticket);


        stockBot.execute(SendMessage.builder()
                .chatId(idChat)
                .text(text)
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .build());
        return ticket;
    }
}
