package org.rostovpavel.base.utils;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Math {
    //    Calculate growth as a percentage
    public static @NotNull BigDecimal calculateGrowthAsPercentage(BigDecimal A, @NotNull BigDecimal B) {
        BigDecimal subtract = B.subtract(A);
        BigDecimal multiply = A.multiply(BigDecimal.valueOf(100));
        return subtract.divide(multiply, 3, RoundingMode.HALF_UP);
    }
}
