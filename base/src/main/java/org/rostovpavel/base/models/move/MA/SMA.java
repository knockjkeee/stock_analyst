package org.rostovpavel.base.models.move.MA;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class SMA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal sma20;
    String _keySma20;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal sma50;
    String _keySma50;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal sma100;
    String _keySma100;

    public String getGraphic(BigDecimal price) {
        StringBuilder stringBuilder = new StringBuilder("\n");
        List<EMAData> emaData = intiData(price);
        List<EMAData> sorted = emaData.stream()
                .sorted((a,b) -> b.item.compareTo(a.item))
                .collect(Collectors.toList());
        sorted.forEach(e -> stringBuilder.append(Graphic.getContent(e.name()))
                .append("\t\t\t\t"));
        return stringBuilder.toString();
    }

    private List<EMAData> intiData(BigDecimal price) {
        return List.of(
                new EMAData("ema20", getSma20()),
                new EMAData("ema50", getSma50()),
                new EMAData("ema100", getSma100()),
                new EMAData("price", price)
        );
    }

    private record EMAData(String name, BigDecimal item) {
    }

    @Getter
    private enum Graphic {
        EMA20("ema20","\uD83D\uDFE0"),
        EMA50("ema50","🔵"),
        EMA100("ema100","🔴"),
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
                case "ema20" -> EMA20.getContent();
                case "ema50" -> EMA50.getContent();
                case "ema100" -> EMA100.getContent();
                case "price" -> PRICE.getContent();
                default -> throw new IllegalStateException("Unexpected value");
            };
        }

    }
}
