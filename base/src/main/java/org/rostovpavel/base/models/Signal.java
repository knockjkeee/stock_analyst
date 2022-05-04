package org.rostovpavel.base.models;

public enum Signal {
    BUY("BUY"),
    SELL("SELL"),
    NONE("");

    private final String value;

    Signal(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
