package org.rostovpavel.base.models;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;


@Value
@Builder
public class Stock {
    BigDecimal open;
    BigDecimal close;
    BigDecimal high;
    BigDecimal low;
    long volume;
    String date;
}
