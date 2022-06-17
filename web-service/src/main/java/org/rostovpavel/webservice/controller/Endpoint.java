package org.rostovpavel.webservice.controller;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.rostovpavel.base.dto.TickersDTO;
import org.rostovpavel.base.models.Signal;
import org.rostovpavel.base.models.Ticker;
import org.rostovpavel.base.models.TickerRequestBody;
import org.rostovpavel.webservice.TEMPO.GenerateFile;
import org.rostovpavel.webservice.services.TickerDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/api")
public record Endpoint(TickerDataService tickerDataService) {

    //private final TickerDataService tickerDataService;

    public Endpoint(TickerDataService tickerDataService) {
        this.tickerDataService = tickerDataService;
    }

    @GetMapping("{ticker}")
    public Ticker getDataByTicker(@PathVariable String ticker) {
        return tickerDataService.getDataByTicker(ticker);
    }

    @PostMapping("/data")
    public TickersDTO getDataByTickers(@RequestBody TickerRequestBody data) {
        return tickerDataService.getDataByTickers(data);
    }

    @Contract(value = " -> new", pure = true)
    @NotNull
    private String @NotNull [] getNameTickers() {
        return new String[]{"ASTR", "TAL", "WISH", "U", "RIOT", "LPL", "GPS", "ATVI", "SNAP", "TWTR", "MDLZ", "BAC", "JD", "KO", "INTC", "BABA", "F", "AA", "CCL", "COIN", "COTY", "ET", "ENPH", "EQT", "FSLR", "MSTR", "PCG", "RRC", "SWN", "SPOT", "VIPS", "ZS", "CNK", "CLOV", "ENDP", "MOMO", "PBF", "HOOD", "SPCE", "AAPL", "META", "NVDA", "SAVE", "DDOG", "MOS", "UAA", "BYND", "PTON", "VALE", "RIG", "FTI", "LI", "AAL", "CCXI", "VIR", "SAVA", "ZY", "IOVA", "GTHX", "RKLB", "DKNG", "PLUG", "BBBY", "RIDE", "NOK", "RIVN", "TSLA", "BLUE"};

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
                .filter(e -> ((e.getHMACD() == 3 && e.getHMACDHistogram() == 3 && e.getHPrice() == 3) || (e.getHMACD()
                        == -3 && e.getHMACDHistogram() == -3 && e.getHPrice() == -3
                ) || (e.getHPrice() >= 1 && e.getHMACD() == 3 && e.getHMACDHistogram() > 1 && e.getHAO() == 3)
                ))
                .collect(Collectors.toList());
        GenerateFile.writeToJson(history, "history");


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
}
