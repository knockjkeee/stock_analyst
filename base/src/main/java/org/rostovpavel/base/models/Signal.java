package org.rostovpavel.base.models;

public enum Signal {
    BUY("BUY"),
    BUYPLUS("BUY++"),
    SELL("SELL"),
    SELLMINUS("SELL--"),
    VAlHIGH("HIGH"),
    VAlMEDIUM("MEDIUM"),
    VAlLOW("LOW"),
    VAlSMALL("SMALL"),
    NONE("");

    private final String value;

    Signal(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
