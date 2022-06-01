package org.rostovpavel.base.models.power.ADX;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rostovpavel.base.models.IndicatorPowerTrend;
import org.rostovpavel.base.models.Signal;

import java.math.BigDecimal;
import java.math.RoundingMode;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AverageDirectionalMovementIndex implements IndicatorPowerTrend {
    BigDecimal adx;
    BigDecimal dlP;
    BigDecimal dlM;
    BigDecimal procent;
    String _key;
    int scoreKey;
    int scoreLine;

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
        if (Signal.VAlSMALL.getValue().equals(_key)) {
            sum += 12;
            temp += 12;
        }
        if (Signal.VAlLOW.getValue().equals(_key)) {
            sum += 25;
            temp += 25;
        }
        if (Signal.VAlMEDIUM.getValue().equals(_key)) {
            sum += 50;
            temp += 50;
        }
        if (Signal.VAlHIGH.getValue().equals(_key)) {
            sum += 75;
            temp += 75;
        }
        setScoreKey(temp);
        return sum;
    }

    @Override
    public int getScoreToLine(int sum, BigDecimal price) {
        int scoreLine = 0;
        if (dlP.compareTo(dlM) > 0){
            BigDecimal procent;
            try {
                procent = generateProcent(dlM, dlP);
            } catch (Exception e) {
                procent = BigDecimal.ZERO;
            }

            setProcent(procent);
            if (procent.compareTo(BigDecimal.valueOf(20)) > 0) {
                sum += 25;
                scoreLine += 25;
            }
        }else{
            BigDecimal procent;
            try {
                procent = generateProcent(dlP, dlM);
            } catch (Exception e) {
                procent = BigDecimal.ZERO;
            }

            setProcent(procent);
            if (procent.compareTo(BigDecimal.valueOf(20)) > 0) {
                sum -= 25;
                scoreLine -= 25;
            }
        }
        setScoreLine(scoreLine);
        return sum;
    }


    private BigDecimal generateProcent(BigDecimal one, BigDecimal two){
        return one.divide(two, 5, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).subtract(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).abs();
    }
}
