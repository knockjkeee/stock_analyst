package org.rostovpavel.base.models.RSI;

import lombok.Builder;
import lombok.Data;
import org.rostovpavel.base.models.Indicator;

import java.math.BigDecimal;

@Data
@Builder
public class RelativeStrengthIndex implements Indicator {
    int upLine;
    BigDecimal currentRSI;
    int downLine;
    String _key;

    @Override
    public int getScore() {
        return 0;
    }
}
