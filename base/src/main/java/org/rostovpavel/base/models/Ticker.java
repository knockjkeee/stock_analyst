package org.rostovpavel.base.models;


import lombok.Builder;
import lombok.Data;
import org.rostovpavel.base.models.ATR.AverageTrueRange;
import org.rostovpavel.base.models.BB.BollingerBands;
import org.rostovpavel.base.models.CCI.CommodityChannel;
import org.rostovpavel.base.models.MA.MovingAverage;
import org.rostovpavel.base.models.MACD.MovingAverageConvergenceDivergence;
import org.rostovpavel.base.models.RSI.RelativeStrengthIndex;
import org.rostovpavel.base.models.RSI_SO.RelativeStrengthIndexStochastic;
import org.rostovpavel.base.models.SO.StochasticOscillator;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Data
@Builder
public class Ticker {
    String name;
    BigDecimal price;
    int candle;
    int score;
    int powerVal;
    MovingAverage movingAverage;
    RelativeStrengthIndex rsi;
    RelativeStrengthIndexStochastic stochRSI;
    CommodityChannel cci;
    StochasticOscillator stochasticOscillator;
    MovingAverageConvergenceDivergence macd;
    BollingerBands bollingerBands;
    AverageTrueRange atr;

    public int getScoreIndicators() {
        List<Indicator> indicators = getIndicators();
        return indicators.stream().mapToInt(ind -> ind.getScore(price)).sum();
    }

    private List<Indicator> getIndicators() {
        return Arrays.asList(movingAverage, rsi, stochRSI, cci, stochasticOscillator, macd, bollingerBands, atr);
    }
}
