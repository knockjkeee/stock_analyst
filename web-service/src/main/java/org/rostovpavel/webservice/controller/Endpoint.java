package org.rostovpavel.webservice.controller;

import org.rostovpavel.base.dto.TickersDTO;
import org.rostovpavel.base.models.Ticker;
import org.rostovpavel.base.models.TickerRequestBody;
import org.rostovpavel.webservice.services.TickerDataService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

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


    @GetMapping("/test")
    public TickersDTO getTest(){
        List<String> date = Arrays.asList(
                "CLOV", "ENDP", "TAL", "SPCE", "ASTR", "SNAP", "CCL", "CNK", "RIG", "FTI", "LI", "WISH", "AAL", "CCXI", "VIR", "SAVA", "ZY", "DNLI", "F", "AA"
        );
        TickerRequestBody tickerRequestBody = new TickerRequestBody();
        tickerRequestBody.setTickers(date);
        return tickerDataService.getDataByTickers(tickerRequestBody);
    }
}
