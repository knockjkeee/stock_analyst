package org.rostovpavel.base.models.MA;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SMA {
    BigDecimal sma20;
    String signalSma20;
    BigDecimal sma50;
    String signalSma50;
    BigDecimal sma100;
    String signalSma100;
}
