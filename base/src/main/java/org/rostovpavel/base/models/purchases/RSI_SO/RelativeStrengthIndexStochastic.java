package org.rostovpavel.base.models.purchases.RSI_SO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rostovpavel.base.models.IndicatorPurchases;
import org.rostovpavel.base.models.Signal;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RelativeStrengthIndexStochastic implements IndicatorPurchases {
    double upLine;
    BigDecimal currentStochRSI;
    double downLine;
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
            sum += 25;
            temp += 25;
        }
        if (Signal.SELL.getValue().equals(_key)) {
            sum -= 25;
            temp -= 25;
        }
        setScoreToKeys(temp);
        return sum;
    }

    @Override
    public int getScoreToLine(int sum, BigDecimal price) {
        int temp = 0;
        if (currentStochRSI.compareTo(BigDecimal.valueOf(downLine)) < 0) {
            sum += 25;
            temp += 25;
        }
        if (currentStochRSI.compareTo(BigDecimal.valueOf(upLine)) > 0) {
            sum -= 25;
            temp -= 25;
        }
        setScoreToLine(temp);
        return sum;
    }
}
