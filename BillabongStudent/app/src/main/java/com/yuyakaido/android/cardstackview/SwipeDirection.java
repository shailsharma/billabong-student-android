package com.yuyakaido.android.cardstackview;

import java.util.Arrays;
import java.util.List;

public enum SwipeDirection {
    Left, Right, Top, Bottom;

    public static final List<SwipeDirection> FREEDOM = Arrays
            .asList(SwipeDirection.values());
    public static final List<SwipeDirection> FREEDOM_NO_BOTTOM = Arrays
            .asList(SwipeDirection.Top, SwipeDirection.Left, SwipeDirection.Right);
    public static final List<SwipeDirection> HORIZONTAL = Arrays
            .asList(SwipeDirection.Left, SwipeDirection.Right);
    public static final List<SwipeDirection> VERTICAL = Arrays
            .asList(SwipeDirection.Top, SwipeDirection.Bottom);
    public static final List<SwipeDirection> LEFT = Arrays
            .asList(SwipeDirection.Left);
    public static final List<SwipeDirection> RIGHT = Arrays
            .asList(SwipeDirection.Right);
    public static final List<SwipeDirection> TOP = Arrays
            .asList(SwipeDirection.Top);
    public static final List<SwipeDirection> BOTTOM = Arrays
            .asList(SwipeDirection.Bottom);

    public static List<SwipeDirection> from(int value) {
        switch (value) {
            case 0:
                return FREEDOM;
            case 1:
                return FREEDOM_NO_BOTTOM;
            case 2:
                return HORIZONTAL;
            case 3:
                return VERTICAL;
            case 4:
                return LEFT;
            case 5:
                return RIGHT;
            case 6:
                return TOP;
            case 7:
                return BOTTOM;
            default:
                return FREEDOM;
        }
    }
}
