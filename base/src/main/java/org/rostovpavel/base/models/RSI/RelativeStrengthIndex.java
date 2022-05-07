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
        if (Signal.BUY.getValue().equals(_key)) {
            sum += 50;
        }
        if (Signal.SELL.getValue().equals(_key)) {
            sum -= 50;
        }
        return sum;
    }

    @Override
    public int getScoreToLine(int sum, BigDecimal price) {
        if (currentRSI.compareTo(BigDecimal.valueOf(upLine)) > 0) {
            sum -= 50;
        }
        if (currentRSI.compareTo(BigDecimal.valueOf(downLine)) < 0) {
            sum +=50;
        }
        return sum;
    }
}
