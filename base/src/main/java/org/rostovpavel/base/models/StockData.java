package org.rostovpavel.base.models;


import lombok.Builder;
import lombok.Value;
import org.rostovpavel.base.dto.StocksDTO;

@Value
@Builder
public class StockData {
    String name;
    StocksDTO candle;
}
