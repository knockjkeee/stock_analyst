package org.rostovpavel.base.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.rostovpavel.base.models.move.AO.AwesomeOscillator;
import org.rostovpavel.base.models.move.BB.BollingerBands;
import org.rostovpavel.base.models.move.MA.MovingAverage;
import org.rostovpavel.base.models.move.MACD.MovingAverageConvergenceDivergence;
import org.rostovpavel.base.models.move.ST.SuperTrend;
import org.rostovpavel.base.models.power.ADX.AverageDirectionalMovementIndex;
import org.rostovpavel.base.models.power.ATR.AverageTrueRange;
import org.rostovpavel.base.models.purchases.CCI.CommodityChannel;
import org.rostovpavel.base.models.purchases.RSI.RelativeStrengthIndex;
import org.rostovpavel.base.models.purchases.RSI_SO.RelativeStrengthIndexStochastic;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ticker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    @Column(precision = 19, scale = 2)
    @Type(type = "big_decimal")
    BigDecimal price;
    int hPrice;
    int hMACD;
    int hMACDHistogram;
    String hMACDProcent;
    String hMACDProcentRES;
    int hAO;
    int hAODirection;
    int HAOColor;
    int hBB;
    int candle;
    int scoreMove;
    int scorePowerVal;
    int scorePowerTrend;
    int scorePurchases;
    String time;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "movingAverage_id")
    MovingAverage movingAverage;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "macd_id")
    MovingAverageConvergenceDivergence macd;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "bollingerBands_id")
    BollingerBands bollingerBands;
    //StochasticOscillator stochasticOscillator;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "awesomeOscillator_id")
    AwesomeOscillator awesomeOscillator;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "superTrend_id")
    SuperTrend superTrend;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "rsi_id")
    RelativeStrengthIndex rsi;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "stochRSI_id")
    RelativeStrengthIndexStochastic stochRSI;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "cci_id")
    CommodityChannel cci;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "atr_id")
    AverageTrueRange atr;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "adx_id")
    AverageDirectionalMovementIndex adx;


    public void generateScoreIndicators() {
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
        //return Arrays.asList(movingAverage, macd, bollingerBands, stochasticOscillator, awesomeOscillator, superTrend);
        return Arrays.asList(movingAverage, macd, bollingerBands, awesomeOscillator, superTrend);
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
