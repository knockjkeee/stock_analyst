package org.rostovpavel.base.models.MA;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MovingAverage {
    SMA sma;
    EMA ema;
}
