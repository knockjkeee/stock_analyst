package org.rostovpavel.base.models.move.SO;

import lombok.Builder;
import lombok.Data;
import org.rostovpavel.base.models.Indicator;
import org.rostovpavel.base.models.IndicatorMove;
import org.rostovpavel.base.models.Signal;

import java.math.BigDecimal;

@Data
@Builder
public class StochasticOscillator implements IndicatorMove {
    int upLine;
    BigDecimal currentK;
    BigDecimal currentD;
    int downLine;
    String _key;
    int scoreToKeys;
    int scoreToLine;
    int scoreToSignal;

    @Override
    public int getScore(BigDecimal price) {
        return prepareScore(price);
    }

    @Override
    public int prepareScore(BigDecimal price) {
        int sum = 0;
        sum = getScoreToKey(sum, price);
        sum = getScoreToLine(sum, price);
        sum = getScoreToSignal(sum, price);
        return sum;
    }

    @Override
    public int getScoreToKey(int sum, BigDecimal price) {
        int temp = 0;
        if (Signal.BUY.getValue().equals(_key)) {
            sum += 50;
            temp += 50;
        }
        if (Signal.SELL.getValue().equals(_key)) {
            sum -= 50;
            temp -= 50;
        }
        setScoreToKeys(temp);
        return sum;
    }

    @Override
    public int getScoreToLine(int sum, BigDecimal price) {
        int temp = 0;
        //TODO check !=0
        if (getScoreToKeys() != 0) {
            if ((currentD.compareTo(BigDecimal.valueOf(downLine)) < 0)
                    && (currentK.compareTo(BigDecimal.valueOf(downLine)) < 0)
            ) {
                sum += 25;
                temp += 25;
            }
            if ((currentD.compareTo(BigDecimal.valueOf(upLine)) > 0)
                    && (currentK.compareTo(BigDecimal.valueOf(upLine)) > 0)
            ) {
                sum -= 25;
                temp -= 25;
            }
            setScoreToLine(temp);
        }
        return sum;
    }

    private int getScoreToSignal(int sum, BigDecimal price) {
        int temp = 0;
        if (currentD.compareTo(currentK) < 0) {
            sum += 25;
            temp += 25;
        }
        if (currentD.compareTo(currentK) > 0) {
            sum -= 25;
            temp -= 25;
        }
        setScoreToSignal(temp);
        return sum;
    }
}
