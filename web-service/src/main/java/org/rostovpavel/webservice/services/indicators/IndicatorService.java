package org.rostovpavel.webservice.services.indicators;

import org.rostovpavel.base.dto.StocksDTO;
import org.rostovpavel.base.models.Indicator;

public interface IndicatorService {
    Indicator getData(StocksDTO data);
}
