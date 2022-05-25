package org.rostovpavel.base.models.move.ST;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rostovpavel.base.models.IndicatorMove;
import org.rostovpavel.base.models.Signal;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SuperTrend implements IndicatorMove {

    BigDecimal mainTrend;
    String _keyMain;
    BigDecimal secondTrend;
    String _keySecond;
    int scoreKey;

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
        int key = 0;
        if (_keyMain.equals(Signal.BUY.getValue()) && _keySecond.equals(Signal.BUY.getValue())) {
            key += 50;
            sum += 50;
        }
        if (_keyMain.equals(Signal.SELL.getValue()) && _keySecond.equals(Signal.SELL.getValue())) {
            key -= 50;
            sum -= 50;
        }
        if (_keyMain.equals(Signal.BUY.getValue()) && _keySecond.equals(Signal.SELL.getValue())) {
            key += 25;
            sum += 25;
        }
        if (_keyMain.equals(Signal.SELL.getValue()) && _keySecond.equals(Signal.BUY.getValue())) {
            key -= 25;
            sum -= 25;
        }
        setScoreKey(key);
        return sum;
    }

    @Override
    public int getScoreToLine(int sum, BigDecimal price) {
        return sum;
    }
}
