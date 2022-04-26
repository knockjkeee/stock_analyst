package org.rostovpavel.base.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class TickerRequestBody {
    private List<String> tickers;
}
