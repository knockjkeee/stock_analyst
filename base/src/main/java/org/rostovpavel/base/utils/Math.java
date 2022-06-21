package org.rostovpavel.base.utils;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Math {
    //    Calculate growth as a percentage
    //    % = (B-A)/A*100
    public static @NotNull BigDecimal calculateGrowthAsPercentage(BigDecimal A, @NotNull BigDecimal B) {
        BigDecimal subtract = B.subtract(A);
        BigDecimal divide = subtract.divide(A, 6, RoundingMode.HALF_UP);
        if (A.compareTo(BigDecimal.valueOf(0)) < 0 && B.compareTo(BigDecimal.valueOf(0)) > 0) {
            return divide.multiply(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(-1));
        }
        return divide.multiply(BigDecimal.valueOf(100));
    }
}
