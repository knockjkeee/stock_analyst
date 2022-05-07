package org.rostovpavel.base.models;

import java.math.BigDecimal;

public interface Indicator {
    int getScore(BigDecimal price);

    int prepareScore(BigDecimal price);

    int getScoreToKey(int sum, BigDecimal price);

    int getScoreToLine(int sum, BigDecimal price);
}
