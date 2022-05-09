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
    int scoreVolatility;

    @Override
    public int getScore(BigDecimal price) {
        return prepareScore(price);
    }

    @Override
    public int prepareScore(BigDecimal price) {
        int sum = 0;
        sum = getScoreToKey(sum, price);
        sum = getScoreToLine(sum, price);
        setScoreVolatility(sum);
        return 0;
    }

    @Override
    public int getScoreToKey(int sum, BigDecimal price) {
        if (Signal.VAlSMALL.getValue().equals(_key)) {
            sum += 25;
        }
        if (Signal.VAlLOW.getValue().equals(_key)) {
            sum += 50;
        }
        if (Signal.VAlMEDIUM.getValue().equals(_key)) {
            sum += 75;
        }
        if (Signal.VAlHIGH.getValue().equals(_key)) {
            sum += 100;
        }
        return sum;
    }

    @Override
    public int getScoreToLine(int sum, BigDecimal price) {
        return sum;
    }
}
