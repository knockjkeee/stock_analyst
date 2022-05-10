package org.rostovpavel.webservice.controller;

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
    public TickersDTO getDataByTickers(@RequestBody TickerRequestBody data){
        return tickerDataService.getDataByTickers(data);
    }

    @NotNull
    private String[] getNameTickers() {
        return new String[]{"CLOV", "ENDP", "TAL", "SPCE", "ASTR", "SNAP", "CCL", "CNK", "RIG", "FTI", "LI", "WISH", "AAL", "CCXI", "VIR", "SAVA", "ZY", "DNLI", "F", "AA"};
    }

    @GetMapping("/all")
    public TickersDTO getTest(){
        List<String> date = Arrays.asList(
                getNameTickers()
        );
        TickerRequestBody tickerRequestBody = new TickerRequestBody();
        tickerRequestBody.setTickers(date);
        return tickerDataService.getDataByTickers(tickerRequestBody);
    }

    @GetMapping("/move")
    public TickersDTO gatFilterMove(){
        List<String> date = Arrays.asList(
                getNameTickers()
        );
        TickerRequestBody tickerRequestBody = new TickerRequestBody();
        tickerRequestBody.setTickers(date);
        List<Ticker> collect = tickerDataService.getDataByTickers(tickerRequestBody).getStocks()
                .stream().filter(e -> e.getScoreMove() > 120 || e.getScoreMove() < -120).collect(Collectors.toList());
        return new TickersDTO(collect);
    }

    @GetMapping("/val")
    public TickersDTO getFilterVal(){
        List<String> date = Arrays.asList(
                getNameTickers()
        );
        TickerRequestBody tickerRequestBody = new TickerRequestBody();
        tickerRequestBody.setTickers(date);
        List<Ticker> collect = tickerDataService.getDataByTickers(tickerRequestBody).getStocks()
                .stream().filter(e -> e.getScorePower() > 0).collect(Collectors.toList());
        return new TickersDTO(collect);
    }

    @GetMapping("/pur")
    public TickersDTO getFilterPurchases(){
        List<String> date = Arrays.asList(
                getNameTickers()
        );
        TickerRequestBody tickerRequestBody = new TickerRequestBody();
        tickerRequestBody.setTickers(date);
        List<Ticker> collect = tickerDataService.getDataByTickers(tickerRequestBody).getStocks()
                .stream()
                .filter(e -> e.getScorePurchases() < -100 && e.getScorePurchases() > 100)
                .collect(Collectors.toList());
        return new TickersDTO(collect);
    }
}
