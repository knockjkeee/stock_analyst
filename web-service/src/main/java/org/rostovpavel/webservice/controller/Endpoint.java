package org.rostovpavel.webservice.controller;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.rostovpavel.base.dto.TickersDTO;
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
        return new String[]{"ASTR", "TAL", "WISH", "U", "RIOT", "LPL", "GPS", "ATVI", "SNAP", "TWTR", "MDLZ", "BAC", "JD", "KO", "INTC", "BABA", "F", "AA", "CCL", "COIN", "COTY", "ET", "ENPH", "EPAM", "EQT", "FSLR", "MSTR", "PCG", "RRC", "REGI", "SEDG", "SWN", "SPOT", "VEON", "VIPS", "ZS", "CNK", "CLOV", "ENDP", "MOMO", "PBF", "HOOD", "SPCE", "AAPL", "FB", "NVDA", "SAVE", "TSLA", "DDOG", "MOS", "UAA", "BYND", "PTON", "VALE", "RIG", "FTI", "LI", "AAL", "CCXI", "VIR", "SAVA", "ZY", "DNLI"};



//        "ASTR", "TAL", "WISH", "U", "RIOT", "LPL", "GPS", "ATVI", "SNAP", "TWTR", "MDLZ", "BAC", "JD", "KO", "INTC", "BABA", "F", "AA", "CCL", "COIN", "COTY", "ET", "ENPH", "EPAM", "EQT", "FSLR", "MSTR", "PCG", "RRC", "REGI", "SEDG", "SWN", "SPOT", "VEON", "VIPS", "ZS", "CNK", "CLOV", "ENDP", "MOMO", "PBF", "HOOD", "SPCE", "AAPL", "FB", "NVDA", "SAVE", "TSLA", "DDOG", "MOS", "UAA", "BYND", "PTON", "VALE", "RIG", "FTI", "LI", "AAL", "CCXI", "VIR", "SAVA", "ZY", "DNLI"


    }

    @GetMapping("/all")
    public TickersDTO getTest() {
        List<String> date = Arrays.asList(
                getNameTickers()
        );
        TickerRequestBody tickerRequestBody = new TickerRequestBody();
        tickerRequestBody.setTickers(date);
        return tickerDataService.getDataByTickers(tickerRequestBody);
    }

    @Contract(" -> new")
    @GetMapping("/move")
    public @NotNull TickersDTO gatFilterMove() {
        List<String> date = Arrays.asList(
                getNameTickers()
        );
        TickerRequestBody tickerRequestBody = new TickerRequestBody();
        tickerRequestBody.setTickers(date);
        List<Ticker> collect = tickerDataService.getDataByTickers(tickerRequestBody).getStocks()
                .stream().filter(e -> e.getScoreMove() > 120 || e.getScoreMove() < -120).collect(Collectors.toList());
        return new TickersDTO(collect);
    }

    @Contract(" -> new")
    @GetMapping("/up")
    public @NotNull TickersDTO gatFilterUp() {
        List<String> date = Arrays.asList(
                getNameTickers()
        );
        TickerRequestBody tickerRequestBody = new TickerRequestBody();
        tickerRequestBody.setTickers(date);
        List<Ticker> collect = tickerDataService.getDataByTickers(tickerRequestBody).getStocks()
//                .stream().filter(e ->
//                                ((e.getScoreMove() > 150 || e.getScoreMove() < -150)
//                                    && (e.getScorePowerTrend() > 50 || e.getScorePowerTrend() < -50))
//                                || ((e.getScoreMove() > 150 || e.getScoreMove() < -150)
//                                    && (e.getScorePowerTrend() > 25 || e.getScorePowerTrend() < -25)
//                                    && (e.getScorePurchases() != 0))
//                                || ((e.getScoreMove() > 120 || e.getScoreMove() < -120)
//                                    && (e.getScorePowerTrend() > 20 || e.getScorePowerTrend() < -20)
//                                    && (e.getScorePurchases() != 0))
//                ).collect(Collectors.toList());
                .stream().filter(e ->
                        ((e.getScoreMove() > 175 || e.getScoreMove() < -150))
//                                && (e.getScorePowerTrend() > 50 || e.getScorePowerTrend() < -50))
//                                || ((e.getScoreMove() > 150 || e.getScoreMove() < -125)
//                                && (e.getScorePowerTrend() != 0)
//                                && (e.getScorePurchases() != 0))
//                                || ((e.getScoreMove() > 150 || e.getScoreMove() < -125))
//                                || ((e.getScoreMove() > 120 || e.getScoreMove() < -100)
//                                && (e.getScorePowerVal() !=0)
//                                && (e.getScorePowerTrend() !=0)
//                                && (e.getScorePurchases() != 0))
                ).collect(Collectors.toList());
        GenerateFile.writeToJson(collect);
        return new TickersDTO(collect);
    }

    @Contract(" -> new")
    @GetMapping("/csv")
    public ResponseEntity<String> generateCSV() {
        GenerateFile.generateCSV();
        return new ResponseEntity<>("Success", HttpStatus.OK);
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
        List<String> date = Arrays.asList(
                getNameTickers()
        );
        TickerRequestBody tickerRequestBody = new TickerRequestBody();
        tickerRequestBody.setTickers(date);
        List<Ticker> collect = tickerDataService.getDataByTickers(tickerRequestBody).getStocks()
                .stream().filter(e -> e.getScorePowerVal() != 0).collect(Collectors.toList());
        return new TickersDTO(collect);
    }

    @Contract(" -> new")
    @GetMapping("/trend")
    public @NotNull TickersDTO getFilterTrend() {
        List<String> date = Arrays.asList(
                getNameTickers()
        );
        TickerRequestBody tickerRequestBody = new TickerRequestBody();
        tickerRequestBody.setTickers(date);
        List<Ticker> collect = tickerDataService.getDataByTickers(tickerRequestBody).getStocks()
                .stream().filter(e -> e.getScorePowerTrend() > 0).collect(Collectors.toList());
        return new TickersDTO(collect);
    }

    @Contract(" -> new")
    @GetMapping("/pur")
    public @NotNull TickersDTO getFilterPurchases() {
        List<String> date = Arrays.asList(
                getNameTickers()
        );
        TickerRequestBody tickerRequestBody = new TickerRequestBody();
        tickerRequestBody.setTickers(date);
        List<Ticker> collect = tickerDataService.getDataByTickers(tickerRequestBody).getStocks()
                .stream()
                .filter(e -> e.getScorePurchases() < -50 || e.getScorePurchases() > 50)
                .collect(Collectors.toList());
        return new TickersDTO(collect);
    }
}
