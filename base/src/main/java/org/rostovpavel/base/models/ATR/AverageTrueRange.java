package org.rostovpavel.base.models.ATR;

import lombok.Builder;
import lombok.Data;
import org.rostovpavel.base.models.Indicator;
import org.rostovpavel.base.models.Signal;

import java.math.BigDecimal;

@Data
@Builder
public class AverageTrueRange implements Indicator {
    BigDecimal atr;
    BigDecimal stopLoseLong;
    BigDecimal stopLoseShort;
    String _key;
    int scoreToKeys;

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
            sum += 25;
            temp += 25;
        }
        if (Signal.SELL.getValue().equals(_key)) {
            sum -= 25;
            temp -= 25;
        }
        if (Signal.BUYPLUS.getValue().equals(_key)) {
            sum += 50;
            temp += 50;
        }
        if (Signal.SELLMINUS.getValue().equals(_key)) {
            sum -= 50;
            temp -= 50;
        }
        setScoreToKeys(temp);
        return sum;
    }

    @Override
    public int getScoreToLine(int sum, BigDecimal price) {
        return sum;
    }
}
