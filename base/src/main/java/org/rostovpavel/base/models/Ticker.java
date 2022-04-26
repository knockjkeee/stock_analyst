package org.rostovpavel.base.models;


import lombok.Builder;
import lombok.Value;
import org.rostovpavel.base.models.MA.MovingAverage;
import org.rostovpavel.base.models.RSI.RelativeStrengthIndex;

import java.math.BigDecimal;

@Value
@Builder
public class Ticker {
    String name;
    BigDecimal price;
    MovingAverage ma;
    RelativeStrengthIndex rsi;
}
