package org.rostovpavel.base.models.RSI_SO;

import lombok.Builder;
import lombok.Data;
import org.rostovpavel.base.models.Indicator;

import java.math.BigDecimal;

@Data
@Builder
public class RelativeStrengthIndexStochastic implements Indicator {
    double upLine;
    BigDecimal currentStochRSI;
    double downLine;
}
