package org.rostovpavel.webservice.controller;

import org.rostovpavel.base.dto.StockDTO;
import org.rostovpavel.base.dto.StockDataDTO;
import org.rostovpavel.base.models.MA.MovingAverage;
import org.rostovpavel.base.models.TickerData;
import org.rostovpavel.webservice.services.MAService;
import org.rostovpavel.webservice.services.StockService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api")
public record Endpoint(StockService StockService, MAService maService) {

    @GetMapping("{ticker}")
    public StockDTO getCandleByTicker(@PathVariable String ticker) {
        StockService.ema();
        StockDTO stockDataByTicker = StockService.getStockDataByTicker(ticker);
        MovingAverage movingAverage = maService.getMovingAverage(stockDataByTicker);
        return stockDataByTicker;
    }

    @PostMapping("/data")
    public StockDataDTO getCandleByTickers(@RequestBody TickerData data){
        return StockService.getStockDataByTikers(data);
    }
}
