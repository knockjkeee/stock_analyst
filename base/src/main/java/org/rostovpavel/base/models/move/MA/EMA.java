package org.rostovpavel.base.models.move.MA;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
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
