package org.rostovpavel.base.models.RSI;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RelativeStrengthIndex {
    int upLine;
    BigDecimal currentRSI;
    int downLine;
    String signal;
}
