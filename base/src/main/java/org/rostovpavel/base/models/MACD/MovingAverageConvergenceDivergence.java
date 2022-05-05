package org.rostovpavel.base.models.MACD;

import lombok.Builder;
import lombok.Data;
import org.rostovpavel.base.models.Indicator;

import java.math.BigDecimal;

@Data
@Builder
public class MovingAverageConvergenceDivergence implements Indicator {
    BigDecimal MACD;
    BigDecimal signal;
    BigDecimal histogram;
    String _key;
}
