package org.rostovpavel.base.models.ATR;

import lombok.Builder;
import lombok.Data;
import org.rostovpavel.base.models.Indicator;

import java.math.BigDecimal;

@Data
@Builder
public class AverageTrueRange implements Indicator {
    BigDecimal atr;
    BigDecimal stopLoseLong;
    BigDecimal stopLoseShort;
}
