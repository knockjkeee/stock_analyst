package org.rostovpavel.webservice.controller;

import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.rostovpavel.base.dto.TickersDTO;
import org.rostovpavel.base.models.Signal;
import org.rostovpavel.base.models.Ticker;
import org.rostovpavel.base.models.TickerRequestBody;
import org.rostovpavel.webservice.TEMPO.GenerateFile;
import org.rostovpavel.webservice.services.TickerDataService;
import org.rostovpavel.webservice.telegram.Bot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/api")
public class Endpoint {

    private final TickerDataService tickerDataService;
    private final Bot bot;

    @Value("${telegram.idChat}")
    private String idChat;


    public Endpoint(TickerDataService tickerDataService, Bot bot) {
        this.tickerDataService = tickerDataService;
        this.bot = bot;
    }

    @SneakyThrows
    @GetMapping("{ticker}")
    public Ticker getDataByTicker(@PathVariable String ticker) {
        Ticker ticket = tickerDataService.getDataByTicker(ticker);
        bot.execute(SendMessage.builder()
                .chatId(idChat)
                .text(getNamedByTicket(ticket))
//                .parseMode(ParseMode.MARKDOWNV2)
                .build());
        return ticket;
    }

    @PostMapping("/data")
    public TickersDTO getDataByTickers(@RequestBody TickerRequestBody data) {
        return tickerDataService.getDataByTickers(data);
    }

    @Contract(value = " -> new", pure = true)
    @NotNull
    private String @NotNull [] getNameTickers() {
        return new String[]{"ASTR", "TAL", "WISH", "U", "RIOT", "LPL", "GPS", "ATVI", "SNAP", "TWTR", "MDLZ", "BAC", "JD", "KO", "INTC", "BABA", "F", "AA", "CCL", "COIN", "COTY", "ET", "ENPH", "EQT", "FSLR", "MSTR", "PCG", "RRC", "SWN", "SPOT", "VIPS", "ZS", "CNK", "CLOV", "ENDP", "MOMO", "PBF", "HOOD", "SPCE", "AAPL", "META", "NVDA", "SAVE", "DDOG", "MOS", "UAA", "BYND", "PTON", "VALE", "RIG", "FTI", "LI", "AAL", "CCXI", "VIR", "SAVA", "ZY", "IOVA", "GTHX", "RKLB", "DKNG", "PLUG", "BBBY", "RIDE", "NOK", "RIVN", "TSLA", "BLUE", "SQ", "EAR", "AWH"};

//        "ASTR", "TAL", "WISH", "U", "RIOT", "LPL", "GPS", "ATVI", "SNAP", "TWTR", "MDLZ", "BAC", "JD", "KO", "INTC", "BABA", "F", "AA", "CCL", "COIN", "COTY", "ET", "ENPH", "EPAM", "EQT", "FSLR", "MSTR", "PCG", "RRC", "REGI", "SEDG", "SWN", "SPOT", "VEON", "VIPS", "ZS", "CNK", "CLOV", "ENDP", "MOMO", "PBF", "HOOD", "SPCE", "AAPL", "FB", "NVDA", "SAVE", "TSLA", "DDOG", "MOS", "UAA", "BYND", "PTON", "VALE", "RIG", "FTI", "LI", "AAL", "CCXI", "VIR", "SAVA", "ZY", "DNLI"


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
        List<Ticker> stocks = tickerDataService.getDataByTickers(tickerRequestBody).getStocks();
        //write all
        GenerateFile.writeToJson(stocks, "All_stocks");
        //filter
        List<Ticker> collect = stocks.stream()
                .filter(e -> ((e.getScoreMove() > 300 && e.getScorePowerTrend() != 0 && e.getScorePowerVal() != 0)
                        || (e.getScoreMove() > 300 && e.getScorePowerTrend() != 0)
                        || (e.getScoreMove() > 175 && e.getScorePowerTrend() != 0 && e.getScorePowerVal() != 0)
                        || (e.getScoreMove() > 175 && e.getScoreMove() < 300 && e.getScorePowerVal() != 0)
                        || (e.getScoreMove() < -175 && e.getScorePowerTrend() != 0 && e.getScorePowerVal() != 0)
                ))
                .collect(Collectors.toList());
        //write filter
        GenerateFile.writeToJson(collect, "filter");

        List<Ticker> lastFilter = stocks.stream()
                .filter(e -> ((((e.getSuperTrend().get_keyMain().equals(Signal.BUYPLUS.getValue()) || e.getSuperTrend()
                        .get_keyMain()
                        .equals(Signal.SELLMINUS.getValue())
                ) || (e.getMacd().get_key().equals(Signal.BUYPLUS.getValue()) || e.getMacd()
                        .get_key()
                        .equals(Signal.SELLMINUS.getValue())
                ) || (e.getAwesomeOscillator().get_key().equals(Signal.BUY.getValue()) || e.getAwesomeOscillator()
                        .get_key()
                        .equals(Signal.SELL.getValue())
                )
                )
                        && (e.getScoreMove() > 175 || e.getScoreMove() < -175)
                        && e.getScorePowerVal() != 0
                        && e.getMovingAverage().getInnerScore() != 0
                ) || ((e.getSuperTrend().get_keyMain().equals(Signal.BUYPLUS.getValue()) && e.getSuperTrend()
                        .get_keySecond()
                        .equals(Signal.BUYPLUS.getValue())
                ) || (e.getSuperTrend().get_keyMain().equals(Signal.SELLMINUS.getValue()) && e.getSuperTrend()
                        .get_keySecond()
                        .equals(Signal.SELLMINUS.getValue())
                )
                ) || (e.getScoreMove() >= 375 || e.getScoreMove() <= -375) || (e.getAwesomeOscillator()
                        .getSaucerScanner()
                        .equals(Signal.BUYPLUS.getValue()) || e.getAwesomeOscillator()
                        .getSaucerScanner()
                        .equals(Signal.SELLMINUS.getValue()) || e.getAwesomeOscillator()
                        .getTwinPeakScanner()
                        .equals(Signal.BUYPLUS.getValue()) || e.getAwesomeOscillator()
                        .getTwinPeakScanner()
                        .equals(Signal.SELLMINUS.getValue())
                )
                ))
                .collect(Collectors.toList());
        GenerateFile.writeToJson(lastFilter, "last");

//        where h_price>=1 and hMACD='3' and HMACDHISTOGRAM>1 and HAO=3 Order by name
        List<Ticker> history = stocks.stream()
                .filter(e -> ((e.getHMACD() == 3 && e.getHMACDHistogram() == 3 && e.getHPrice() == 3 &&  e.getHAO() >= 2 && e.getHAODirection() > 1)
                        || (e.getHMACD() == -3 && e.getHMACDHistogram() == -3 && e.getHPrice() == -3 &&  e.getHAO() <= -2 && e.getHAODirection() < 1)
                        || (e.getHPrice() >= 1 && e.getHMACD() == 3 && e.getHMACDHistogram() > 1 && e.getHAO() == 3 && e.getHAODirection() > 1)
                ) && e.getHMACDProcentResult().compareTo(BigDecimal.valueOf(0)) > 0)
                .collect(Collectors.toList());
        GenerateFile.writeToJson(history, "history");

        history.forEach(e ->  {
            try {
                bot.execute(SendMessage.builder()
                        .chatId(idChat)
                        .text(getNamedByTicket(e))
    //                .parseMode(ParseMode.MARKDOWNV2)
                        .build());
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        });

        // return filter
        return new TickersDTO(history);
    }

    @Contract(" -> new")
    @GetMapping("/csv")
    public @NotNull ResponseEntity<String> generateCSV() {
        GenerateFile.generateCSV("All_stocks");
        GenerateFile.generateCSV("filter");
        GenerateFile.generateCSV("last");
        GenerateFile.generateCSV("history");

        return new ResponseEntity<>("Success filter and All_stocks", HttpStatus.OK);
    }


    @GetMapping(value = "/foo.csv")
    @ResponseBody
    public String fooAsCSV(HttpServletResponse response) {
        response.setContentType("text/plain; charset=utf-8");
        return "a,b,c\n1,2,3\n3,4,5";
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

    public String getNamedByTicket(Ticker ticker) {
        String res = "\t*" + ticker.getName() + "* \t\t " + ticker.getPrice() + " \t\t " + ticker.getSuperTrend().get_keyMain() + "/" + ticker.getSuperTrend().get_keySecond() + " \t\t " + ticker.getTime()
                + "\n*Mov/Val/Tre/Pur/MA*" + " \t\t " +  ticker.getScoreMove() +"/" +ticker.getScorePowerVal() +"/" + ticker.getScorePowerTrend() + "/" + ticker.getScorePurchases()+"/"+ ticker.getMovingAverage().getInnerScore()
                + "\n hPrice/hMACD/hMACDHis" + " \t\t " + ticker.getHPrice() +"/" + ticker.getHMACD() +"/" + ticker.getHMACDHistogram()
                + "\n" + ticker.getHMACDProcent()
                + "\n" + ticker.getHMACDProcentHis()
                + "\n" + ticker.getHMACDProcentResult()
                + "\nhAO/hAODir/hAOCol/hBB" + " \t\t " + ticker.getHAO() +"/" + ticker.getHAODirection() +"/" + ticker.getHAOColor() +"/" + ticker.getHBB();

//
//         SString.valueOf(e.getHAO()),
//                        String.valueOf(e.getHAODirection()),
//                        String.valueOf(e.getHAOColor()),
//                        String.valueOf(e.getHBB()),
        return res;
//        return replaceAllBindCharacter(res);
    }



    private String replaceAllBindCharacter(String text) {
        return text.replaceAll("\\.", ",");
//                .replaceAll("]", "/")
//                .replace('[', '/')
//                .replaceAll("-", "/")
//                .replaceAll("\\.", "")
//                .replaceAll(">", "/")
//                .replace("(", "/")
//                .replace(")", "/");
    }
}
