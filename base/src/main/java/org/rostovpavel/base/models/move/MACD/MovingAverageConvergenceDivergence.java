package org.rostovpavel.base.models.move.MACD;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.rostovpavel.base.models.IndicatorMove;
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
public class MovingAverageConvergenceDivergence implements IndicatorMove {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal MACD;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal signal;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal histogram;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal procent;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal widthLine;
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
        sum = getScoreToLine(sum, price);
        sum = getScoreToKey(sum, price);
        sum = getScoreToSignal(sum, price);
        return sum;
    }

    @Override
    public int getScoreToKey(int sum, BigDecimal price) {
        int scoreKey = 0;
        if (getScoreToLine() != 0) {
            if (Signal.BUY.getValue().equals(_key)) {
                sum += 25;
                scoreKey += 25;
            }
            if (Signal.SELL.getValue().equals(_key)) {
                sum -= 25;
                scoreKey -= 25;
            }
            if (Signal.BUYPLUS.getValue().equals(_key)) {
                sum += 100;
                scoreKey += 100;
            }
            if (Signal.SELLMINUS.getValue().equals(_key)) {
                sum -= 100;
                scoreKey -= 100;
            }
        }
        setScoreToKeys(scoreKey);
        return sum;
    }

    @Override
    public int getScoreToLine(int sum, BigDecimal price) {
        int scoreLine = 0;
        if (histogram.compareTo(BigDecimal.valueOf(0)) > 0) {
            sum += 25;
            scoreLine += 25;
        }
        if (histogram.compareTo(BigDecimal.valueOf(0)) < 0) {
            sum -= 25;
            scoreLine -= 25;
        }
        setScoreToLine(scoreLine);
        return sum;
    }

    private int getScoreToSignal(int sum, BigDecimal price) {
        int scoreSignal = 0;
        if (MACD.compareTo(signal) > 0) {
            BigDecimal procent;
            try {
                procent = calculateGrowthAsPercentage(signal, MACD);
            } catch (Exception e) {
                procent = BigDecimal.ZERO;
            }
            setProcent(procent);
            if (procent.compareTo(BigDecimal.valueOf(10)) > 0 || procent.compareTo(BigDecimal.valueOf(-10)) < 0) {
                sum += 25;
                scoreSignal += 25;
            }
        }
        if (MACD.compareTo(signal) < 0) {
            BigDecimal procent;
            try {
                procent = calculateGrowthAsPercentage(MACD, signal);
            } catch (Exception e) {
                procent = BigDecimal.ZERO;
            }
            setProcent(procent);
            if (procent.compareTo(BigDecimal.valueOf(10)) > 0 || procent.compareTo(BigDecimal.valueOf(-10)) < 0) {
                sum -= 25;
                scoreSignal -= 25;
            }
        }
        setScoreToSignal(scoreSignal);
        return sum;
    }
}
