package org.rostovpavel.base.models;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@AllArgsConstructor
@Value
public class StockDTO {
    List<Stock> stocks;
}
