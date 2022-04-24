package org.rostovpavel.base.models.MA;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MovingAverage {
    EMA ema;
    SMA sma;
}
