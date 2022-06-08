package org.rostovpavel.base.models.power.ATR;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.rostovpavel.base.models.IndicatorPowerVal;
import org.rostovpavel.base.models.Signal;

import javax.persistence.*;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class AverageTrueRange implements IndicatorPowerVal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal atr;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal stopLoseLong;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
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
        return sum;
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
