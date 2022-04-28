package org.rostovpavel.base.models.RSI_Stochastic;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RelativeStrengthIndexStochastic {
    double upLine;
    BigDecimal currentStochRSI;
    double downLine;
}
