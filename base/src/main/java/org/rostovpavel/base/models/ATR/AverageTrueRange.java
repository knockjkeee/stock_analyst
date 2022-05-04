package org.rostovpavel.base.models.ATR;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AverageTrueRange {
    BigDecimal atr;
    BigDecimal stopLoseLong;
    BigDecimal stopLoseShort;
}
