package org.rostovpavel.base.models.move.ST;

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
public class SuperTrend implements IndicatorMove {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal mainTrend;
    String _keyMain;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal secondTrend;
    String _keySecond;
    int scoreKey;

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
        int key = 0;
//        if ((_keyMain.equals(Signal.BUY.getValue()) || _keyMain.equals(Signal.BUYPLUS.getValue()))
//                && (_keySecond.equals(Signal.BUY.getValue()) || _keySecond.equals(Signal.BUYPLUS.getValue()))) {
//            key += 50;
//            sum += 50;
//        }
//        if ((_keyMain.equals(Signal.SELL.getValue()) || _keyMain.equals(Signal.SELLMINUS.getValue()))
//                && (_keySecond.equals(Signal.SELL.getValue()) || _keySecond.equals(Signal.SELLMINUS.getValue()))) {
//            key -= 50;
//            sum -= 50;
//        }
//        if ((_keyMain.equals(Signal.BUY.getValue()) || _keyMain.equals(Signal.BUYPLUS.getValue()))
//                && (_keySecond.equals(Signal.SELL.getValue()) || _keySecond.equals(Signal.SELLMINUS.getValue()))) {
//            key += 25;
//            sum += 25;
//        }
//        if ((_keyMain.equals(Signal.SELL.getValue()) || _keyMain.equals(Signal.SELLMINUS.getValue()))
//                && (_keySecond.equals(Signal.BUY.getValue()) || _keySecond.equals(Signal.BUYPLUS.getValue()))) {
//            key -= 25;
//            sum -= 25;
//        }
//////////////////
        if ((_keyMain.equals(Signal.BUY.getValue()) && _keySecond.equals(Signal.BUY.getValue()))
                || (_keyMain.equals(Signal.BUYPLUS.getValue()) && _keySecond.equals(Signal.BUYPLUS.getValue()))) {
            key += 50;
            sum += 50;
        }
        if ((_keyMain.equals(Signal.SELL.getValue()) && _keySecond.equals(Signal.SELL.getValue()))
                || (_keyMain.equals(Signal.SELLMINUS.getValue()) && _keySecond.equals(Signal.SELLMINUS.getValue()))) {
            key -= 50;
            sum -= 50;
        }

        if ((_keyMain.equals(Signal.SELL.getValue()) && _keySecond.equals(Signal.SELLMINUS.getValue()))
                || (_keyMain.equals(Signal.SELL.getValue()) && _keySecond.equals(Signal.BUY.getValue()))
                || (_keyMain.equals(Signal.SELL.getValue()) && _keySecond.equals(Signal.BUYPLUS.getValue()))
                || (_keyMain.equals(Signal.SELLMINUS.getValue()) && _keySecond.equals(Signal.SELL.getValue()))
                || (_keyMain.equals(Signal.SELLMINUS.getValue()) && _keySecond.equals(Signal.BUY.getValue()))
                || (_keyMain.equals(Signal.SELLMINUS.getValue()) && _keySecond.equals(Signal.BUYPLUS.getValue()))) {
            key -= 25;
            sum -= 25;
        }
        if ((_keyMain.equals(Signal.BUY.getValue()) && _keySecond.equals(Signal.BUYPLUS.getValue()))
                || (_keyMain.equals(Signal.BUY.getValue()) && _keySecond.equals(Signal.SELL.getValue()))
                || (_keyMain.equals(Signal.BUY.getValue()) && _keySecond.equals(Signal.SELLMINUS.getValue()))
                || (_keyMain.equals(Signal.BUYPLUS.getValue()) && _keySecond.equals(Signal.BUY.getValue()))
                || (_keyMain.equals(Signal.BUYPLUS.getValue()) && _keySecond.equals(Signal.SELL.getValue()))
                || (_keyMain.equals(Signal.BUYPLUS.getValue()) && _keySecond.equals(Signal.SELLMINUS.getValue()))) {
            key += 25;
            sum += 25;
        }

        setScoreKey(key);
        return sum;
    }

    @Override
    public int getScoreToLine(int sum, BigDecimal price) {
        int key = 0;
        if (_keyMain.equals(Signal.BUYPLUS.getValue())) {
            key += 50;
            sum += 50;
        }
        if (_keyMain.equals(Signal.SELLMINUS.getValue())) {
            key -= 50;
            sum -= 50;
        }
        setScoreKey(getScoreKey() + key);
        return sum;
    }

    public String graphicItem() {
        StringBuilder stringBuilder = new StringBuilder("\n");
        stringBuilder.append("Main(30,5): ");
        stringBuilder.append(getMainTrend()).append("\t\t");
        stringBuilder.append(get_keyMain());
        stringBuilder.append("\t\t\t\t");
        stringBuilder.append("\nSecond(10,2): ");
        stringBuilder.append(getSecondTrend()).append("\t\t");
        stringBuilder.append(get_keySecond());
        return stringBuilder.toString().trim();
    }

}
