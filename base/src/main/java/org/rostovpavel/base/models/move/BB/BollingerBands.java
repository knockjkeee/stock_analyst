package org.rostovpavel.base.models.move.BB;

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

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class BollingerBands implements IndicatorMove {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal middleBand;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal upperBand;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal lowerBand;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal widthBand;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal wbProcent;
    int scoreToKeys;
    int scoreToLine;
    int scoreToSignal;
    String _key;

    @Override
    public int getScore(BigDecimal price) {
        return prepareScore(price);
    }

    @Override
    public int prepareScore(BigDecimal price) {
        int sum = 0;
        sum = getScoreToKey(sum, price);
        sum = getScoreToLine(sum, price);
        sum = getScoreToSignal(sum, price);
        return sum;
    }

    @Override
    public int getScoreToKey(int sum, BigDecimal price) {
        int scoreKey = 0;
        if (Signal.BUY.getValue().equals(_key)) {
            sum += 50;
            scoreKey += 50;
        }
        if (Signal.SELL.getValue().equals(_key)) {
            sum -= 50;
            scoreKey -= 50;
        }
        setScoreToKeys(scoreKey);
        return sum;
    }

    @Override
    public int getScoreToLine(int sum, BigDecimal price) {
        int scoreLine = 0;
        if (wbProcent.compareTo(BigDecimal.valueOf(2)) > 0) {
            if ((price.compareTo(upperBand) < 0) && (price.compareTo(middleBand) > 0)) {
                sum += 25;
                scoreLine += 25;
            }
            if ((price.compareTo(lowerBand) > 0) && (price.compareTo(middleBand) < 0)) {
                sum -= 25;
                scoreLine -= 25;
            }
        }
        setScoreToLine(scoreLine);
        return sum;
    }

    private int getScoreToSignal(int sum, BigDecimal price) {
        int scoreSignal = 0;
        if (getScoreToLine() > 0) {
            BigDecimal diffMiddle = generateDiffMiddle(upperBand, middleBand, "UP");
            if (((price.compareTo(upperBand) <= 0) && (price.compareTo(diffMiddle) >= 0))
                    || ((price.compareTo(upperBand) >= 0) && (price.compareTo(diffMiddle) >= 0))) {
                sum += 50;
                scoreSignal += 50;
            }
        }
        if (getScoreToLine() < 0) {
            BigDecimal diffMiddle = generateDiffMiddle(middleBand, lowerBand, "DOWN");
            if (((price.compareTo(lowerBand) >= 0) && (price.compareTo(diffMiddle) <= 0))
                    || ((price.compareTo(lowerBand) <= 0) && (price.compareTo(diffMiddle) <= 0))) {
                sum -= 50;
                scoreSignal -= 50;
            }
        }
        setScoreToSignal(scoreSignal);
        return sum;
    }

    private BigDecimal generateDiffMiddle(BigDecimal one, BigDecimal two, String direction) {
        BigDecimal diff = (one.subtract(two)).divide(BigDecimal.valueOf(2), 5, RoundingMode.HALF_UP);
        if (direction.equals("UP")) {
            return middleBand.add(diff);
        } else {
            return middleBand.subtract(diff);
        }
    }

    public String graphicItem(BigDecimal price) {
        StringBuilder stringBuilder = new StringBuilder("\n");
        stringBuilder.append("(");
        stringBuilder.append(getWbProcent().setScale(1, RoundingMode.UP));
        stringBuilder.append(" %");
        stringBuilder.append(" /");
        stringBuilder.append(" ");
        stringBuilder.append(getWidthBand().setScale(1, RoundingMode.UP));
        stringBuilder.append(")");
        stringBuilder.append("\t");
        List<BBData> emaData = intiData(price);
        List<BBData> sorted = emaData.stream()
                .sorted((a,b) -> b.item.compareTo(a.item))
                .collect(Collectors.toList());
        sorted.forEach(e -> stringBuilder.append(Graphic.getContent(e.name()))
                .append("\t"));
        return stringBuilder.toString().trim();
    }

    private List<BBData> intiData(BigDecimal price) {
        return List.of(
                new BBData("upperBand", getUpperBand()),
                new BBData("middleBand", getMiddleBand()),
                new BBData("lowerBand", getLowerBand()),
                new BBData("doublemiddleup", generateDiffMiddle(getUpperBand(), getMiddleBand(), "UP")),
                new BBData("doublemiddledown", generateDiffMiddle(getMiddleBand(), getLowerBand(), "DOWN")),
                new BBData("price", price)
        );
    }

    private record BBData(String name, BigDecimal item) {}

    @Getter
    private enum Graphic {
        UPPERBAND("upperBand","◾️"),
        MIDDLEBAND("middleBand","\uD83D\uDFE0"),
        LOWERBAND("lowerBand","◾️"),
        DOUBLEMIDDLEUP("doublemiddleup","\uD83D\uDD39"),
        DOUBLEMIDDLEDOWN("doublemiddledown","\uD83D\uDD38"),
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
                case "upperBand" -> UPPERBAND.getContent();
                case "middleBand" -> MIDDLEBAND.getContent();
                case "lowerBand" -> LOWERBAND.getContent();
                case "doublemiddleup" -> DOUBLEMIDDLEUP.getContent();
                case "doublemiddledown" -> DOUBLEMIDDLEDOWN.getContent();
                case "price" -> PRICE.getContent();
                default -> throw new IllegalStateException("Unexpected value");
            };
        }

    }


}
