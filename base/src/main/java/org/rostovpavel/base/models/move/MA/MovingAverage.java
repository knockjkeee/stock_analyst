package org.rostovpavel.base.models.move.MA;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
public class MovingAverage implements IndicatorMove {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name = "sma_id")
    SMA sma;

    @OneToOne(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name = "ema_id")
    EMA ema;
    int innerScore;

    @Override
    public int getScore(BigDecimal price) {
        int i = prepareScore(price);
        setInnerScore(i);
        return i;
    }

    @Override
    public int prepareScore(BigDecimal price) {
        int sum = 0;
        //KEY + LINE
        sum = getScoreToKey(sum, price);
        return sum;
    }

    @Override
    public int getScoreToKey(int sum, BigDecimal price ) {
        sum = getScoreToBUY(sum, price);
        sum = getScoreToSELL(sum, price);
        return sum;
    }

    @Override
    public int getScoreToLine(int sum, BigDecimal price) {
        return sum;
    }

    private int getScoreToBUY(int sum, BigDecimal price) {
        if (Signal.BUY.getValue().equals(getSma()._keySma20)) {
            sum += 25;
        }
        if (Signal.BUY.getValue().equals(getSma()._keySma50)) {
            sum += 25;
        }
        if (Signal.BUY.getValue().equals(getSma()._keySma100)) {
            sum += 25;
        }
        sum = getLineScoreToBUY(sum, price);
        return sum;
    }

    private int getScoreToSELL(int sum, BigDecimal price) {
        if (Signal.SELL.getValue().equals(getSma()._keySma20)) {
            sum -= 25;
        }
        if (Signal.SELL.getValue().equals(getSma()._keySma50)) {
            sum -= 25;
        }
        if (Signal.SELL.getValue().equals(getSma()._keySma100)) {
            sum -= 25;
        }
        sum = getLineScoreToSELL(sum, price);
        return sum;
    }

    private int getLineScoreToBUY(int sum, BigDecimal price) {
        if ((price.compareTo(getSma().sma20) > 0)
                && (getSma().sma20.compareTo(getSma().sma50) > 0)
                && (getSma().sma50.compareTo(getSma().sma100) > 0)){
            sum += 75;
        }
        else if ((price.compareTo(getSma().sma20) > 0)
                        && (getSma().sma20.compareTo(getSma().sma50) > 0)) {
            sum += 50;
        }
        else if (price.compareTo(getSma().sma20) > 0) {
            sum += 25;
        }
        return sum;
    }

    private int getLineScoreToSELL(int sum, BigDecimal price) {
        if ((price.compareTo(getSma().sma20) < 0)
                && (getSma().sma20.compareTo(getSma().sma50) < 0)
                && (getSma().sma50.compareTo(getSma().sma100) < 0)){
            sum -= 75;
        }
        else if ((price.compareTo(getSma().sma20) < 0)
                && (getSma().sma20.compareTo(getSma().sma50) < 0)) {
            sum -= 50;
        }
        else if (price.compareTo(getSma().sma20) < 0) {
            sum -= 25;
        }
        return sum;
    }
}
