package org.rostovpavel.webservice.controller;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.rostovpavel.base.dto.TickersDTO;
import org.rostovpavel.base.models.Ticker;
import org.rostovpavel.base.models.TickerRequestBody;
import org.rostovpavel.webservice.services.TickerDataService;
import org.springframework.web.bind.annotation.*;

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
        return new String[]{"CLOV", "ENDP", "TAL", "SPCE", "ASTR", "SNAP", "CCL", "CNK", "RIG", "FTI", "LI", "WISH", "AAL", "CCXI", "VIR", "SAVA", "ZY", "DNLI", "F", "AA"};
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
                .stream().filter(e -> ((e.getScoreMove() > 150 || e.getScoreMove() < -150)
                        && (e.getScorePowerTrend() > 50 || e.getScorePowerTrend() < -50)) ||
                        ((e.getScoreMove() > 150 || e.getScoreMove() < -150)
                                && (e.getScorePowerTrend() > 25 || e.getScorePowerTrend() < -25)
                                && (e.getScorePurchases() != 0))
                ).collect(Collectors.toList());
        return new TickersDTO(collect);
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
                .stream().filter(e -> e.getScorePowerVal() > 0).collect(Collectors.toList());
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
