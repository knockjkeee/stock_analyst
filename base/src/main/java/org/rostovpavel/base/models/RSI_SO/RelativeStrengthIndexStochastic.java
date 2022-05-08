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
    int scoreToLine;

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
        //TODO add KEY
        return sum;
    }

    @Override
    public int getScoreToLine(int sum, BigDecimal price) {
        int temp = 0;
        if (currentStochRSI.compareTo(BigDecimal.valueOf(downLine)) < 0) {
            sum +=25;
            temp +=25;
        }
        if (currentStochRSI.compareTo(BigDecimal.valueOf(upLine)) > 0) {
            sum -= 25;
            temp -= 25;
        }
        setScoreToLine(temp);
        return sum;
    }
}
