package org.rostovpavel.base.models.MACD;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MovingAverageConvergenceDivergence {
    BigDecimal MACD;
    BigDecimal signal;
    BigDecimal histogram;
}
