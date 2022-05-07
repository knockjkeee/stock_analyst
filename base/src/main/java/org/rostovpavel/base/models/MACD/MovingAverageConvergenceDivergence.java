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

    @Override
    public int getScore(BigDecimal price) {
        return 0;
    }

    @Override
    public int prepareScore(BigDecimal price) {
        return 0;
    }

    @Override
    public int getScoreToKey(int sum, BigDecimal price) {
        return 0;
    }

    @Override
    public int getScoreToLine(int sum, BigDecimal price) {
        return 0;
    }
}
