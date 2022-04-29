package org.rostovpavel.base.models;


import lombok.Builder;
import lombok.Value;
import org.rostovpavel.base.models.CCI.CommodityChannel;
import org.rostovpavel.base.models.MA.MovingAverage;
import org.rostovpavel.base.models.MACD.MovingAverageConvergenceDivergence;
import org.rostovpavel.base.models.RSI.RelativeStrengthIndex;
import org.rostovpavel.base.models.RSI_SO.RelativeStrengthIndexStochastic;
import org.rostovpavel.base.models.SO.StochasticOscillator;

import java.math.BigDecimal;

@Value
@Builder
public class Ticker {
    String name;
    BigDecimal price;
    int candle;
    MovingAverage ma;
    RelativeStrengthIndex rsi;
    RelativeStrengthIndexStochastic stochRSI;
    CommodityChannel cci;
    StochasticOscillator so;
    MovingAverageConvergenceDivergence macd;

}
