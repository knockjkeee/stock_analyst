package org.rostovpavel.base.models.move.MA;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class EMA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal ema20;
    String _keyEma20;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal ema50;
    String _keyEma50;
    @Column(precision = 19, scale = 4)
    @Type(type = "big_decimal")
    BigDecimal ema100;
    String _keyEma100;
}
