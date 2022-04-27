package org.rostovpavel.base.models.CCI;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CommodityChannel {
    int upLine;
    BigDecimal currentCCI;
    int downLine;
}
