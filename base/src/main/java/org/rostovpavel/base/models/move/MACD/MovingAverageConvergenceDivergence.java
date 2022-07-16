package org.rostovpavel.base.models.move.MACD;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.Type;
import org.rostovpavel.base.models.IndicatorMove;
import org.rostovpavel.base.models.Signal;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import static org.rostovpavel.base.utils.Math.calculateGrowthAsPercentage;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovingAverageConvergenceDivergence implements IndicatorMove {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal MACD;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal signal;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal histogram;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal procent;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal widthLine;
    String _key;
    int scoreToKeys;
    int scoreToLine;
    int scoreToSignal;

    @Override
    public int getScore(BigDecimal price) {
        return prepareScore(price);
    }

    @Override
    public int prepareScore(BigDecimal price) {
        int sum = 0;
        sum = getScoreToLine(sum, price);
        sum = getScoreToKey(sum, price);
        sum = getScoreToSignal(sum, price);
        return sum;
    }

    @Override
    public int getScoreToKey(int sum, BigDecimal price) {
        int scoreKey = 0;
        if (getScoreToLine() != 0) {
            if (Signal.BUY.getValue().equals(_key)) {
                sum += 25;
                scoreKey += 25;
            }
            if (Signal.SELL.getValue().equals(_key)) {
                sum -= 25;
                scoreKey -= 25;
            }
            if (Signal.BUYPLUS.getValue().equals(_key)) {
                sum += 100;
                scoreKey += 100;
            }
            if (Signal.SELLMINUS.getValue().equals(_key)) {
                sum -= 100;
                scoreKey -= 100;
            }
        }
        setScoreToKeys(scoreKey);
        return sum;
    }

    @Override
    public int getScoreToLine(int sum, BigDecimal price) {
        int scoreLine = 0;
        if (histogram.compareTo(BigDecimal.valueOf(0)) > 0) {
            sum += 25;
            scoreLine += 25;
        }
        if (histogram.compareTo(BigDecimal.valueOf(0)) < 0) {
            sum -= 25;
            scoreLine -= 25;
        }
        setScoreToLine(scoreLine);
        return sum;
    }

    private int getScoreToSignal(int sum, BigDecimal price) {
        int scoreSignal = 0;
        if (MACD.compareTo(signal) > 0) {
            BigDecimal procent;
            try {
                procent = calculateGrowthAsPercentage(signal, MACD);
            } catch (Exception e) {
                procent = BigDecimal.ZERO;
            }
            setProcent(procent);
            if (procent.compareTo(BigDecimal.valueOf(10)) > 0 || procent.compareTo(BigDecimal.valueOf(-10)) < 0) {
                sum += 25;
                scoreSignal += 25;
            }
        }
        if (MACD.compareTo(signal) < 0) {
            BigDecimal procent;
            try {
                procent = calculateGrowthAsPercentage(MACD, signal);
            } catch (Exception e) {
                procent = BigDecimal.ZERO;
            }
            setProcent(procent);
            if (procent.compareTo(BigDecimal.valueOf(10)) > 0 || procent.compareTo(BigDecimal.valueOf(-10)) < 0) {
                sum -= 25;
                scoreSignal -= 25;
            }
        }
        setScoreToSignal(scoreSignal);
        return sum;
    }

    public String graphicItem() {
        StringBuilder stringBuilder = new StringBuilder("\n");
        stringBuilder.append("(");
        stringBuilder.append(getProcent().setScale(1, RoundingMode.UP));
        stringBuilder.append(" %)");
        stringBuilder.append("\t\t\t\t");
        List<MACDData> emaData = intiData();
        List<MACDData> sorted = emaData.stream()
                .sorted((a,b) -> b.item.compareTo(a.item))
                .collect(Collectors.toList());
        sorted.forEach(e -> stringBuilder.append(Graphic.getContent(e.name()))
                .append("\t\t\t\t"));
        stringBuilder.append(Graphic.getDirectionHistogram(getHistogram()));
        return stringBuilder.toString();
    }

    private List<MACDData> intiData() {
        return List.of(
                new MACDData("signal", getSignal()),
                new MACDData("macd", getMACD())
//                new MACDData("histogram", getHistogram())
        );
    }

    private record MACDData(String name, BigDecimal item) {}

    @Getter
    private enum Graphic {
        SIGNAL("signal","\uD83D\uDFE0"),
        MACD("macd","🔵"),
        HISTOGRAM("histogram","⬜️"),
        PRICE("price","\uD83D\uDCB0")
        ;
        private String name;
        private String content;

        Graphic(String name, String content) {
            this.name = name;
            this.content = content;
        }

        public static String getContent(String value) {
            return switch (value) {
                case "signal" -> SIGNAL.getContent();
                case "macd" -> MACD.getContent();
                case "histogram" -> HISTOGRAM.getContent();
                case "price" -> PRICE.getContent();
                default -> throw new IllegalStateException("Unexpected value");
            };
        }

        private  static String getDirectionHistogram(BigDecimal histogram) {
            return histogram.compareTo(BigDecimal.valueOf(0)) > 0 ? "🟩" : "🟥";
        }
    }
}
