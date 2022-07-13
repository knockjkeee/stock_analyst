package org.rostovpavel.base.models.state;

import lombok.Getter;

@Getter
public enum PowerState {
    FIRE(true, "\uD83D\uDD25\uD83D\uDD25\uD83D\uDD25"),
    ATTENTION(false, "❗️❗️❗️"),
    ;
    private boolean dir;
    private String label;

    PowerState(boolean dir, String label) {
        this.dir = dir;
        this.label = label;
    }

    public static PowerState getState(boolean value) {
        return value ? FIRE : ATTENTION;
    }
}
