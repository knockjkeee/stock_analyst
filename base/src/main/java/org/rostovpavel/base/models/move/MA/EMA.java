package org.rostovpavel.base.models.move.MA;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class EMA {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    BigDecimal ema20;
    String _keyEma20;
    BigDecimal ema50;
    String _keyEma50;
    BigDecimal ema100;
    String _keyEma100;
}
