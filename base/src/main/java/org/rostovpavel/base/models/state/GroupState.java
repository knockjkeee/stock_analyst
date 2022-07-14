package org.rostovpavel.base.models.state;

import lombok.Getter;

@Getter
public enum GroupState {
    FIRST(1, "\uD83E\uDD47"),
    TWO(2, "\uD83E\uDD48"),
    N0NE(100500, ""),
    ;
    private int group;
    private String label;

    GroupState(int group, String label) {
        this.group = group;
        this.label = label;
    }

    public static GroupState getState(int value) {
        return switch (value) {
            case 1 -> FIRST;
            case 2 -> TWO;
            default -> N0NE;
        };
    }
}
