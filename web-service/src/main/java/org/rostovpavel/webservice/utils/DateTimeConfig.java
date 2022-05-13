package org.rostovpavel.webservice.utils;

public enum DateTimeConfig {
    MONDAY(1, 0, 3, 2, 4, 3, 5, 4, 6, 5), //+3 -> +2
    TUESDAY(1, 0, 2, 1, 4, 3, 5, 4, 6, 5), //+3 -> +2
    WEDNESDAY(1, 0, 2, 1, 3, 2, 5, 4, 6, 5), //+3 -> +2
    THURSDAY(1, 0, 2, 1, 3, 2, 4, 3, 5, 4), //+3 -> +2
    FRIDAY(1, 0, 2, 1, 3, 2, 4, 3, 5, 4), //+3 -> +2
    //test day
    SATURDAY(1, 0, 2, 1, 3, 2, 4, 3),
    SUNDAY(1, 0, 2, 1, 3, 2, 4, 3);

    private final int[] value;

    DateTimeConfig(int... value) {
        this.value = value;
    }

    public int[] getValue() {
        return value;
    }
}

