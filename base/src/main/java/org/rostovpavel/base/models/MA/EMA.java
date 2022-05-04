package org.rostovpavel.base.models.MA;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class EMA {
    BigDecimal ema20;
    String signalEma20;
    BigDecimal ema50;
    String signalEma50;
    BigDecimal ema100;
    String signalEma100;
}
