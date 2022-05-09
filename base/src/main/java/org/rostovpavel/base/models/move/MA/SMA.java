package org.rostovpavel.base.models.move.MA;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SMA {
    BigDecimal sma20;
    String _keySma20;
    BigDecimal sma50;
    String _keySma50;
    BigDecimal sma100;
    String _keySma100;
}
