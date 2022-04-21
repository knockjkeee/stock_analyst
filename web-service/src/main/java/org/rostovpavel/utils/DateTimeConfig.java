package org.rostovpavel.utils;

import java.util.Arrays;

public enum DateTimeConfig {
    MONDAY(1, 0, 4, 3, 5, 4, 6, 5),
    TUESDAY(1, 0, 2, 1, 5, 4, 6, 5),
    WEDNESDAY(1, 0, 2, 1, 3, 2, 6, 5),
    THURSDAY(1, 0, 2, 1, 3, 2, 4, 3),
    FRIDAY(1, 0, 2, 1, 3, 2, 4, 3);

    private int[] value;

    DateTimeConfig(int... value) {
        this.value = value;
    }

    public int[] getValue() {
        System.out.println("Arrays.toString(DAY) = " + Arrays.toString(value));
        return value;
    }
}

