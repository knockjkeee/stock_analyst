package org.rostovpavel.base.models.BB;

import lombok.Builder;
import lombok.Data;
import org.rostovpavel.base.models.Indicator;

import java.math.BigDecimal;

@Data
@Builder
public class BollingerBands implements Indicator {
    BigDecimal middleBand;
    BigDecimal upperBand;
    BigDecimal lowerBand;
    BigDecimal widthBand;
}
