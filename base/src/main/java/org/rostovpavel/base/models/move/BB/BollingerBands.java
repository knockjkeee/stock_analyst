package org.rostovpavel.base.models.move.BB;

import lombok.Builder;
import lombok.Data;
import org.rostovpavel.base.models.Indicator;
import org.rostovpavel.base.models.IndicatorMove;
import org.rostovpavel.base.models.Signal;

import java.math.BigDecimal;

@Data
@Builder
public class BollingerBands implements IndicatorMove {
    BigDecimal middleBand;
    BigDecimal upperBand;
    BigDecimal lowerBand;
    BigDecimal widthBand;
    BigDecimal wbProcent;
    int scoreToKeys;
    int scoreToLine;
    int scoreToSignal;
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
        if ( (price.compareTo(upperBand) < 0) && (price.compareTo(middleBand) > 0) ) {
            sum += 25;
            temp += 25;
        }
        if ( (price.compareTo(lowerBand) > 0) && (price.compareTo(middleBand) < 0) ) {
            sum -=25;
            temp -=25;
        }
        setScoreToLine(temp);
        return sum;
    }

    private int getScoreToSignal(int sum, BigDecimal price) {
        int temp = 0;
        //TODO check !=0
        if (getScoreToLine() > 0) {
            if (wbProcent.compareTo(BigDecimal.valueOf(6)) > 0) {
                sum += 25;
                temp += 25;
            }
        }
        if (getScoreToLine() < 0) {
            if (wbProcent.compareTo(BigDecimal.valueOf(6)) > 0) {
                sum -= 25;
                temp -= 25;
            }
        }
        setScoreToSignal(temp);
        return sum;
    }

}
