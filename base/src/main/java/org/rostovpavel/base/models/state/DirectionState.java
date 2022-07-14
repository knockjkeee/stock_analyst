package org.rostovpavel.base.models.state;

import lombok.Getter;

@Getter
public enum DirectionState {
    UP(1, "⬆️\uD83D\uDFE2"),
    DOWN(-1, "⬇️\uD83D\uDD34"),
    NONE(0, "\uD83D\uDD04\uD83D\uDD18"),
    ;
    private int dir;
    private String label;

    DirectionState(int dir, String label) {
        this.dir = dir;
        this.label = label;
    }

    public static DirectionState getState(int value) {
        int res = Integer.compare(value, 0);
        return switch (res){
            case 1 -> UP;
            case -1 -> DOWN;
            case 0 -> NONE;
            default -> throw new IllegalStateException("Unexpected value: " + res);
        };
    }
}
