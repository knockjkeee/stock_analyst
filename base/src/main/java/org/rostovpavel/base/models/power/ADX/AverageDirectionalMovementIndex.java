package org.rostovpavel.base.models.power.ADX;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.rostovpavel.base.models.IndicatorPowerTrend;
import org.rostovpavel.base.models.Signal;

import javax.persistence.*;
import java.math.BigDecimal;

import static org.rostovpavel.base.utils.Math.calculateGrowthAsPercentage;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class AverageDirectionalMovementIndex implements IndicatorPowerTrend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal adx;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal dlP;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal dlM;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
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
            sum += 25;
            temp += 25;
        }
        if (Signal.VAlLOW.getValue().equals(_key)) {
            sum += 50;
            temp += 50;
        }
        if (Signal.VAlMEDIUM.getValue().equals(_key)) {
            sum += 75;
            temp += 75;
        }
        if (Signal.VAlHIGH.getValue().equals(_key)) {
            sum += 100;
            temp += 100;
        }
        setScoreKey(temp);
        return sum;
    }

    @Override
    public int getScoreToLine(int sum, BigDecimal price) {
        int scoreLine = 0;
        if (dlP.compareTo(dlM) > 0){
            BigDecimal percent;
            try {
                percent = calculateGrowthAsPercentage(dlM, dlP);
            } catch (Exception e) {
                percent = BigDecimal.ZERO;
            }
            setProcent(percent);
            if (percent.compareTo(BigDecimal.valueOf(20)) > 0 || percent.compareTo(BigDecimal.valueOf(-20)) < 0) {
                scoreLine += 25;
            }
        }else{
            BigDecimal percent;
            try {
                percent = calculateGrowthAsPercentage(dlP, dlM);
            } catch (Exception e) {
                percent = BigDecimal.ZERO;
            }
            setProcent(percent);
            if (percent.compareTo(BigDecimal.valueOf(20)) > 0 || percent.compareTo(BigDecimal.valueOf(-20)) < 0) {
                scoreLine -= 25;
            }
        }
        setScoreLine(scoreLine);
        return sum;
    }
}
