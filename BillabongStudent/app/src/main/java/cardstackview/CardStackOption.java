package cardstackview;

import java.util.List;

import cardstackview.StackFrom;
import cardstackview.SwipeDirection;

public class CardStackOption {
    public int visibleCount = 3;
    public float swipeThreshold = 0.75f; // Percentage
    public float translationDiff = 12f; // DP
    public float scaleDiff = 0.02f; // Percentage
    public StackFrom stackFrom = StackFrom.DEFAULT;
    public boolean isElevationEnabled = true;
    public boolean isSwipeEnabled = true;
    public int leftOverlay = 0; // Layout Resource ID
    public int rightOverlay = 0; // Layout Resource ID
    public int bottomOverlay = 0; // Layout Resource ID
    public int topOverlay = 0; // Layout Resource ID
    public List<SwipeDirection> swipeForwardDirection = SwipeDirection.RIGHT;
    public List<SwipeDirection> swipeReverseDirection = SwipeDirection.LEFT;
}
