package org.rostovpavel.base.models.move.ST;

import lombok.Builder;
import lombok.Data;
import org.rostovpavel.base.models.IndicatorMove;

import java.math.BigDecimal;

@Data
@Builder
public class SuperTrend implements IndicatorMove {

    String secondTrend;
    String mainTrend;
    String _key;

    @Override
    public int getScore(BigDecimal price) {
        return prepareScore(price);
    }

    @Override
    public int prepareScore(BigDecimal price) {
        int sum = 0;
        sum = getScoreToKey(sum, price);
        sum = getScoreToLine(sum, price);
        return sum;
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
