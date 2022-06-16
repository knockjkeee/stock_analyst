package org.rostovpavel.base.models.move.SO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rostovpavel.base.models.IndicatorMove;
import org.rostovpavel.base.models.Signal;

import java.math.BigDecimal;

import static org.rostovpavel.base.utils.Math.calculateGrowthAsPercentage;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class StochasticOscillator implements IndicatorMove {
    int upLine;
    BigDecimal currentK;
    BigDecimal currentD;
    int downLine;
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
        sum = getScoreToSignal(sum, price);
        sum = getScoreToLine(sum, price);
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
        if ((currentD.compareTo(BigDecimal.valueOf(downLine)) < 0)
                && (currentK.compareTo(BigDecimal.valueOf(downLine)) < 0)
                && getScoreToSignal() !=0) {
            sum += 25;
            scoreLine += 25;
        }
        if ((currentD.compareTo(BigDecimal.valueOf(upLine)) > 0)
                && (currentK.compareTo(BigDecimal.valueOf(upLine)) > 0)
                && getScoreToSignal() !=0) {
            sum -= 25;
            scoreLine -= 25;
        }
        setScoreToLine(scoreLine);
        return sum;
    }

    private int getScoreToSignal(int sum, BigDecimal price) {
        int scoreSignal = 0;
        if (currentD.compareTo(currentK) < 0) {
            BigDecimal procent = calculateGrowthAsPercentage(currentD, currentK);
            setProcent(procent);
            if (procent.compareTo(BigDecimal.valueOf(6)) > 0) {
                sum += 25;
                scoreSignal += 25;
            }
        }
        if (currentD.compareTo(currentK) > 0) {
            BigDecimal procent = calculateGrowthAsPercentage(currentK, currentD);
            setProcent(procent);
            if (procent.compareTo(BigDecimal.valueOf(6)) > 0) {
                sum -= 25;
                scoreSignal -= 25;
            }
        }
        setScoreToSignal(scoreSignal);
        return sum;
    }
}
