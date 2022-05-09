package org.rostovpavel.base.models.move.MA;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class EMA {
    BigDecimal ema20;
    String _keyEma20;
    BigDecimal ema50;
    String _keyEma50;
    BigDecimal ema100;
    String _keyEma100;
}
