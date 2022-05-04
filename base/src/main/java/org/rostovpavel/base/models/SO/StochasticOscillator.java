package org.rostovpavel.base.models.SO;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class StochasticOscillator {
    int upLine;
    BigDecimal currentK;
    BigDecimal currentD;
    int downLine;
    String signal;
}
