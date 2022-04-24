package org.rostovpavel.base.models.MA;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SMA {
    BigDecimal sma20;
    BigDecimal sma50;
    BigDecimal sma100;
}
