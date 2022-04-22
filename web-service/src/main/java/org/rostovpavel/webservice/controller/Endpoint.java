package org.rostovpavel.webservice.controller;

import org.rostovpavel.base.models.StockDTO;
import org.rostovpavel.webservice.services.TinkoffService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api")
public record Endpoint(TinkoffService tinkoffService) {

    @GetMapping("{ticker}")
    public StockDTO getParam(@PathVariable String ticker) {
        return tinkoffService.getCandles(ticker);
    }
}
