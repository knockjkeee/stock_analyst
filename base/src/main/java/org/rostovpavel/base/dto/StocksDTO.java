package org.rostovpavel.base.dto;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.rostovpavel.base.models.Stock;

import java.util.List;

@AllArgsConstructor
@Value
public class StocksDTO {
    List<Stock> stocks;
}
