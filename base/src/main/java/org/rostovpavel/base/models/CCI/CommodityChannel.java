package org.rostovpavel.base.models.CCI;

import lombok.Builder;
import lombok.Data;
import org.rostovpavel.base.models.Indicator;

import java.math.BigDecimal;

@Data
@Builder
public class CommodityChannel implements Indicator {
    int upLine;
    BigDecimal currentCCI;
    int downLine;

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
        return sum;
    }

    @Override
    public int getScoreToLine(int sum, BigDecimal price) {
        if (currentCCI.compareTo(BigDecimal.valueOf(upLine)) > 0) {
            sum -= 25;
        }
        if (currentCCI.compareTo(BigDecimal.valueOf(downLine)) < 0) {
            sum +=25;
        }
        return sum;
    }
}
