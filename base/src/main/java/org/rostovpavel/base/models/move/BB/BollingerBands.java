package org.rostovpavel.base.models.move.BB;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rostovpavel.base.models.IndicatorMove;
import org.rostovpavel.base.models.Signal;

import java.math.BigDecimal;
import java.math.RoundingMode;

@NoArgsConstructor
@AllArgsConstructor
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
        int scoreKey = 0;
        if (Signal.BUY.getValue().equals(_key)) {
            sum += 50;
            scoreKey += 50;
        }
        if (Signal.SELL.getValue().equals(_key)) {
            sum -= 50;
            scoreKey -= 50;
        }
        setScoreToKeys(scoreKey);
        return sum;
    }

    @Override
    public int getScoreToLine(int sum, BigDecimal price) {
        int scoreLine = 0;
        if (wbProcent.compareTo(BigDecimal.valueOf(5)) > 0) {
            if ((price.compareTo(upperBand) < 0) && (price.compareTo(middleBand) > 0)) {
                sum += 25;
                scoreLine += 25;
            }
            if ((price.compareTo(lowerBand) > 0) && (price.compareTo(middleBand) < 0)) {
                sum -= 25;
                scoreLine -= 25;
            }
        }
        setScoreToLine(scoreLine);
        return sum;
    }

    private int getScoreToSignal(int sum, BigDecimal price) {
        int scoreSignal = 0;
        if (getScoreToLine() > 0) {
            BigDecimal diffMiddle = generateDiffMiddle(upperBand, middleBand);
            if ( ((price.compareTo(upperBand) <= 0) && (price.compareTo(diffMiddle) > 0))
                    || ((price.compareTo(upperBand) >= 0) && (price.compareTo(diffMiddle) > 0)) ) {
                sum += 50;
                scoreSignal += 50;
            }
        }
        if (getScoreToLine() < 0) {
            BigDecimal diffMiddle = generateDiffMiddle(middleBand, lowerBand);
            if ( ((price.compareTo(lowerBand) >= 0) && (price.compareTo(diffMiddle) < 0))
                    || ((price.compareTo(lowerBand) <= 0) && (price.compareTo(diffMiddle) < 0)) ) {
                sum -= 50;
                scoreSignal -= 50;
            }
        }
        setScoreToSignal(scoreSignal);
        return sum;
    }

    private BigDecimal generateDiffMiddle(BigDecimal one, BigDecimal two) {
        return middleBand.add((one.subtract(two)).divide(BigDecimal.valueOf(2) , 5, RoundingMode.HALF_UP));
    }

}
