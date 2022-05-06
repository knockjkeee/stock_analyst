package org.rostovpavel.base.models.SO;

import lombok.Builder;
import lombok.Data;
import org.rostovpavel.base.models.Indicator;

import java.math.BigDecimal;

@Data
@Builder
public class StochasticOscillator implements Indicator {
    int upLine;
    BigDecimal currentK;
    BigDecimal currentD;
    int downLine;
    String _key;

    @Override
    public int getScore() {
        return 1;
    }
}
