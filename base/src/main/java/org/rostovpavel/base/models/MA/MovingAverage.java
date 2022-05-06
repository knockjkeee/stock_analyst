package org.rostovpavel.base.models.MA;

import lombok.Builder;
import lombok.Data;
import org.rostovpavel.base.models.Indicator;

@Data
@Builder
public class MovingAverage implements Indicator {
    SMA sma;
    EMA ema;

    @Override
    public int getScore() {
        return 0;
    }
}
