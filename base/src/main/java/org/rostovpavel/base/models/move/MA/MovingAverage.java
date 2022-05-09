package org.rostovpavel.base.models.move.MA;

import lombok.Builder;
import lombok.Data;
import org.rostovpavel.base.models.IndicatorMove;
import org.rostovpavel.base.models.Signal;

import java.math.BigDecimal;

@Data
@Builder
public class MovingAverage implements IndicatorMove {
    SMA sma;
    EMA ema;
    int innerScore;

    @Override
    public int getScore(BigDecimal price) {
        int i = prepareScore(price);
        setInnerScore(i);
        return i;
    }

    @Override
    public int prepareScore(BigDecimal price) {
        int sum = 0;
        //KEY + LINE
        sum = getScoreToKey(sum, price);
        return sum;
    }

    @Override
    public int getScoreToKey(int sum, BigDecimal price ) {
        sum = getScoreToBUY(sum, price);
        sum = getScoreToSELL(sum, price);
        return sum;
    }

    @Override
    public int getScoreToLine(int sum, BigDecimal price) {
        return sum;
    }

    private int getScoreToBUY(int sum, BigDecimal price) {
        if (Signal.BUY.getValue().equals(getSma()._keySma20)) {
            sum += 25;
        }
        if (Signal.BUY.getValue().equals(getSma()._keySma50)) {
            sum += 25;
        }
        if (Signal.BUY.getValue().equals(getSma()._keySma100)) {
            sum += 25;
        }
        sum = getLineScoreToBUY(sum, price);
        return sum;
    }

    private int getScoreToSELL(int sum, BigDecimal price) {
        if (Signal.SELL.getValue().equals(getSma()._keySma20)) {
            sum -= 25;
        }
        if (Signal.SELL.getValue().equals(getSma()._keySma50)) {
            sum -= 25;
        }
        if (Signal.SELL.getValue().equals(getSma()._keySma100)) {
            sum -= 25;
        }
        sum = getLineScoreToSELL(sum, price);
        return sum;
    }

    private int getLineScoreToBUY(int sum, BigDecimal price) {
        if (((price.compareTo(getSma().sma20) > 0)
                && (getSma().sma20.compareTo(getSma().sma50) > 0)
                && (getSma().sma50.compareTo(getSma().sma100) > 0))
                ||
                ((price.compareTo(getSma().sma20) > 0)
                        && (getSma().sma20.compareTo(getSma().sma50) > 0))
        ) {
            sum += 25;
        }
        return sum;
    }

    private int getLineScoreToSELL(int sum, BigDecimal price) {
        if (((price.compareTo(getSma().sma20) < 0)
                && (getSma().sma20.compareTo(getSma().sma50) < 0)
                && (getSma().sma50.compareTo(getSma().sma100) < 0))
                ||
                ((price.compareTo(getSma().sma20) < 0)
                        && (getSma().sma20.compareTo(getSma().sma50) < 0))
        ) {
            sum -= 25;
        }
        return sum;
    }

}
