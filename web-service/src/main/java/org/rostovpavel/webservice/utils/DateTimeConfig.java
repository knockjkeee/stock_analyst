package org.rostovpavel.webservice.utils;

public enum DateTimeConfig {
    MONDAY(1, 0, 3, 2, 4, 3, 5, 4), //+3 -> +2
    TUESDAY(1, 0, 2, 1, 4, 3, 5, 4), //+3 -> +2
    WEDNESDAY(1, 0, 2, 1, 3, 2, 5, 4), //+3 -> +2
    THURSDAY(1, 0, 2, 1, 3, 2, 4, 3),
    FRIDAY(1, 0, 2, 1, 3, 2, 4, 3),
    SUNDAY(1, 0, 2, 1, 3, 2, 4, 3);

    private int[] value;

    DateTimeConfig(int... value) {
        this.value = value;
    }

    public int[] getValue() {
//        System.out.println("Arrays.toString(DAY) = " + Arrays.toString(value));
        return value;
    }
}

