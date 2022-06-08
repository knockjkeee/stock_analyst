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
}
