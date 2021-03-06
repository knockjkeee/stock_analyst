package org.rostovpavel.base.models.purchases.RSI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.rostovpavel.base.models.IndicatorPurchases;
import org.rostovpavel.base.models.Signal;

import javax.persistence.*;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelativeStrengthIndex implements IndicatorPurchases {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    int upLine;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal currentRSI;
    int downLine;
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
            sum += 75;
            temp += 75;
        }
        if (Signal.SELL.getValue().equals(_key)) {
            sum -= 75;
            temp -= 75;
        }
        setScoreToKeys(temp);
        return sum;
    }

    @Override
    public int getScoreToLine(int sum, BigDecimal price) {
        int temp = 0;
        if (currentRSI.compareTo(BigDecimal.valueOf(downLine)) < 0) {
            sum += 25;
            temp += 25;
        }
        if (currentRSI.compareTo(BigDecimal.valueOf(upLine)) > 0) {
            sum -= 25;
            temp -= 25;
        }
        setScoreToLine(temp);
        return sum;
    }
}
