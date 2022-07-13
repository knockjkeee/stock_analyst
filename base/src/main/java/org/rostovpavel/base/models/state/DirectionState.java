package org.rostovpavel.base.models.state;

import lombok.Getter;

@Getter
public enum DirectionState {
    UP(true, "⬆️\uD83D\uDFE2"),
    DOWN(false, "⬇️\uD83D\uDD34"),
    ;
    private boolean dir;
    private String label;

    DirectionState(boolean dir, String label) {
        this.dir = dir;
        this.label = label;
    }

    public static DirectionState getState(boolean value) {
        return value ? UP : DOWN;
    }
}
