package org.rostovpavel.base.models;


import lombok.Builder;
import lombok.Data;
import org.rostovpavel.base.models.power.ATR.AverageTrueRange;
import org.rostovpavel.base.models.move.BB.BollingerBands;
import org.rostovpavel.base.models.purchases.CCI.CommodityChannel;
import org.rostovpavel.base.models.move.MA.MovingAverage;
import org.rostovpavel.base.models.move.MACD.MovingAverageConvergenceDivergence;
import org.rostovpavel.base.models.purchases.RSI.RelativeStrengthIndex;
import org.rostovpavel.base.models.purchases.RSI_SO.RelativeStrengthIndexStochastic;
import org.rostovpavel.base.models.move.SO.StochasticOscillator;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Data
@Builder
public class Ticker {
    String name;
    BigDecimal price;
    int candle;
    int scoreMove;
    int scorePower;
    int scorePurchases;
    MovingAverage movingAverage;
    MovingAverageConvergenceDivergence macd;
    BollingerBands bollingerBands;
    StochasticOscillator stochasticOscillator;

    RelativeStrengthIndex rsi;
    RelativeStrengthIndexStochastic stochRSI;
    CommodityChannel cci;

    AverageTrueRange atr;

    public void generateScoreIndicators(){
        getScoreIndicatorsMove();
        getScoreIndicatorsPower();
        getScoreIndicatorsPurchases();
    }

    private void getScoreIndicatorsMove() {
        List<IndicatorMove> indicators = getIndicatorsMove();
        int sum = indicators.stream().mapToInt(ind -> ind.getScore(price)).sum();
        setScoreMove(sum); ;
    }
    private void getScoreIndicatorsPower() {
        List<IndicatorPower> indicators = getIndicatorsPower();
        int sum = indicators.stream().mapToInt(ind -> ind.getScore(price)).sum();
        setScorePower(sum); ;
    }

    private void getScoreIndicatorsPurchases() {
        List<IndicatorPurchases> indicators = getIndicatorsPurchases();
        int sum = indicators.stream().mapToInt(ind -> ind.getScore(price)).sum();
        setScorePurchases(sum); ;
    }

    private List<IndicatorMove> getIndicatorsMove() {
        return Arrays.asList(movingAverage, macd, bollingerBands, stochasticOscillator);
    }

    private List<IndicatorPower> getIndicatorsPower() {
        return List.of(atr);
    }

    private List<IndicatorPurchases> getIndicatorsPurchases() {
        return Arrays.asList(rsi, stochRSI, cci);
    }
}
