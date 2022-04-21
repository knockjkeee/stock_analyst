package org.rostovpavel.controller;

import org.rostovpavel.models.StockDTO;
import org.rostovpavel.services.TinkoffService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api")
public record Endpoint(TinkoffService tinkoffService) {

    @GetMapping("{ticker}")
    public StockDTO getParam(@PathVariable String ticker) {
        return tinkoffService.getCandles(ticker);
    }
}
