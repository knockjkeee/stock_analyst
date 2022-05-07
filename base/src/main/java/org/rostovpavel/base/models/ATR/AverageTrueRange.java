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
