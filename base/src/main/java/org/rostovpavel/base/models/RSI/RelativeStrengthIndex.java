package org.rostovpavel.base.models.RSI;

import lombok.Builder;
import lombok.Data;
import org.rostovpavel.base.models.Indicator;
import org.rostovpavel.base.models.Signal;

import java.math.BigDecimal;

@Data
@Builder
public class RelativeStrengthIndex implements Indicator {
    int upLine;
    BigDecimal currentRSI;
    int downLine;
    String _key;
    int scoreToKeys;
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
        int temp = 0;
        if (Signal.BUY.getValue().equals(_key)) {
            sum += 75;
            temp += 75;
        }
        if (Signal.SELL.getValue().equals(_key)) {
            sum -= 75;
            temp -= 75;
        }
        setScoreToKeys(temp);
        return sum;
    }

    @Override
    public int getScoreToLine(int sum, BigDecimal price) {
        int temp = 0;
        if (currentRSI.compareTo(BigDecimal.valueOf(downLine)) < 0) {
            sum +=25;
            temp +=25;
        }
        if (currentRSI.compareTo(BigDecimal.valueOf(upLine)) > 0) {
            sum -= 25;
            temp -= 25;
        }
        setScoreToLine(temp);
        return sum;
    }
}
