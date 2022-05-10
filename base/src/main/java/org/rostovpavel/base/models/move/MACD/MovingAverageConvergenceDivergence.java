package org.rostovpavel.base.models.move.MACD;

import lombok.Builder;
import lombok.Data;
import org.rostovpavel.base.models.IndicatorMove;
import org.rostovpavel.base.models.Signal;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@Builder
public class MovingAverageConvergenceDivergence implements IndicatorMove {
    BigDecimal MACD;
    BigDecimal signal;
    BigDecimal histogram;
    BigDecimal procent;
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
        int temp = 0;
        if (histogram.compareTo(BigDecimal.valueOf(0)) > 0) {
            sum += 25;
            temp += 25;
        }
        if (histogram.compareTo(BigDecimal.valueOf(0)) < 0) {
            sum -= 25;
            temp -= 25;
        }
        setScoreToLine(temp);
        return sum;
    }

    private int getScoreToSignal(int sum, BigDecimal price) {
        int temp = 0;
        if (MACD.compareTo(signal) > 0) {
            BigDecimal procent = signal.divide(MACD, 5, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).subtract(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).abs();
            setProcent(procent);
            if (procent.compareTo(BigDecimal.valueOf(7)) > 0){
                sum += 25;
                temp += 25;
            }
            sum += 25;
            temp += 25;
        }
        if (MACD.compareTo(signal) < 0) {
            BigDecimal procent = MACD.divide(signal, 5, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).subtract(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).abs();
            setProcent(procent);
            if (procent.compareTo(BigDecimal.valueOf(7)) > 0){
                sum -= 25;
                temp -= 25;
            }
            sum -= 25;
            temp -= 25;
        }
        setScoreToSignal(temp);
        return sum;
    }
}
