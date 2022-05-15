package org.rostovpavel.base.models;


import lombok.Builder;
import lombok.Data;
import org.rostovpavel.base.models.move.AO.AwesomeOscillator;
import org.rostovpavel.base.models.power.ADX.AverageDirectionalMovementIndex;
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
    int scorePowerVal;
    int scorePowerTrend;
    int scorePurchases;
    MovingAverage movingAverage;
    MovingAverageConvergenceDivergence macd;
    BollingerBands bollingerBands;
    StochasticOscillator stochasticOscillator;
    AwesomeOscillator awesomeOscillator;

    RelativeStrengthIndex rsi;
    RelativeStrengthIndexStochastic stochRSI;
    CommodityChannel cci;

    AverageTrueRange atr;
    AverageDirectionalMovementIndex adx;


    public void generateScoreIndicators(){
        getScoreIndicatorsPowerVal();
        getScoreIndicatorsPowerTrend();
        getScoreIndicatorsPurchases();
        getScoreIndicatorsMove();
    }

    private void getScoreIndicatorsMove() {
        List<IndicatorMove> indicators = getIndicatorsMove();
                                                                //power purchases + adx.getScoreLine()
        int sum = indicators.stream().mapToInt(ind -> ind.getScore(price)).sum() + adx.getScoreLine();
        setScoreMove(sum);
    }

    private void getScoreIndicatorsPowerVal() {
        List<IndicatorPowerVal> indicators = getIndicatorsPowerVal();
        int sum = indicators.stream().mapToInt(ind -> ind.getScore(price)).sum();
        setScorePowerVal(sum);
    }

    private void getScoreIndicatorsPowerTrend() {
        List<IndicatorPowerTrend> indicators = getIndicatorsPowerTrend();
        int sum = indicators.stream().mapToInt(ind -> ind.getScore(price)).sum();
        setScorePowerTrend(sum);
    }

    private void getScoreIndicatorsPurchases() {
        List<IndicatorPurchases> indicators = getIndicatorsPurchases();
        int sum = indicators.stream().mapToInt(ind -> ind.getScore(price)).sum();
        setScorePurchases(sum);
    }

    private List<IndicatorMove> getIndicatorsMove() {
        return Arrays.asList(movingAverage, macd, bollingerBands, stochasticOscillator, awesomeOscillator);
    }

    private List<IndicatorPowerVal> getIndicatorsPowerVal() {
        return List.of(atr);
    }

    private List<IndicatorPowerTrend> getIndicatorsPowerTrend() {
        return List.of(adx);
    }

    private List<IndicatorPurchases> getIndicatorsPurchases() {
        return Arrays.asList(rsi, stochRSI, cci);
    }
}
