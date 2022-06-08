package org.rostovpavel.base.models.move.AO;

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

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class AwesomeOscillator implements IndicatorMove {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal ao;
    String color;
    String direction;
    // String zeroCrossLine;
    String _key;
    String saucerScanner;
    String twinPeakScanner;
    int scoreKey;
    int scoreLine;
    int scoreSignal;


    @Override
    public int getScore(BigDecimal price) {
        return prepareScore(price);
    }

    @Override
    public int prepareScore(BigDecimal price) {
        int sum = 0;
        sum = getScoreToKey(sum, price);
        sum = getScoreToLine(sum, price);
        sum = getScoreSignal(sum, price);
        return sum;
    }

    @Override
    public int getScoreToKey(int sum, BigDecimal price) {
        int key = 0;
        if (Signal.BUY.getValue().equals(_key)) {
            sum += 25;
            key += 25;
        }

        if (Signal.SELL.getValue().equals(_key)) {
            sum -= 25;
            key -= 25;
        }
        setScoreKey(key);
        return sum;
    }

    @Override
    public int getScoreToLine(int sum, BigDecimal price) {
        int line = 0;

        if (direction.equals("Up")) {
            sum += 25;
            line += 25;
        }
        if (direction.equals("Down")) {
            sum -= 25;
            line -= 25;
        }
        if (color.equals("Green")) {
            sum += 25;
            line += 25;
        }
        if (color.equals("Red")) {
            sum -= 25;
            line -= 25;
        }
        if (color.equals("Green") && direction.equals("Down")) {
            sum += 25;
            line += 25;
        }
        if (color.equals("Red") && direction.equals("Up")) {
            sum -= 25;
            line -= 25;
        }
        setScoreLine(line);
        return sum;
    }

    public int getScoreSignal(int sum, BigDecimal price) {
        int signal = 0;
        if (Signal.BUYPLUS.getValue().equals(saucerScanner)) {
            sum += 50;
            signal += 50;
        }
        if (Signal.SELLMINUS.getValue().equals(saucerScanner)) {
            sum -= 50;
            signal -= 50;
        }

        if (Signal.BUYPLUS.getValue().equals(twinPeakScanner)) {
            sum += 50;
            signal += 50;
        }
        if (Signal.SELLMINUS.getValue().equals(twinPeakScanner)) {
            sum -= 50;
            signal -= 50;
        }
        setScoreSignal(signal);
        return sum;
    }
}
