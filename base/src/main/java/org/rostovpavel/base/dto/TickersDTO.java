package org.rostovpavel.base.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rostovpavel.base.models.Ticker;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TickersDTO {
    List<Ticker> stocks;
}
