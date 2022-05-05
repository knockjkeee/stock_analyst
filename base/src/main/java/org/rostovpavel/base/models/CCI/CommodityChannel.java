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
}
