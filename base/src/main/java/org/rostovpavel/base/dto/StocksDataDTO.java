package org.rostovpavel.base.dto;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.rostovpavel.base.models.StockData;

import java.util.List;

@AllArgsConstructor
@Value
public class StocksDataDTO {
    List<StockData> stocks;
}
