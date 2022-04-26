package org.rostovpavel.base.dto;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.rostovpavel.base.models.Ticker;

import java.util.List;

@AllArgsConstructor
@Value
public class TickersDTO {
    List<Ticker> stocks;
}
