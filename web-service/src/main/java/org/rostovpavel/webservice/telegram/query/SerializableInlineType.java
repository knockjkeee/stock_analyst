package org.rostovpavel.webservice.telegram.query;

public enum SerializableInlineType {
    INDICATOR(0),
    HOME(1),
    ;

    private final int index;

    SerializableInlineType(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
