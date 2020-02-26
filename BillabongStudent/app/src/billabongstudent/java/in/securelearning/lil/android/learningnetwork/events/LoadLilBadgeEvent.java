package in.securelearning.lil.android.learningnetwork.events;

import in.securelearning.lil.android.base.dataobjects.LILBadges;

/**
 * Created by Pushkar Raj 31-08-2016.
 */
public class LoadLilBadgeEvent {
    private final LILBadges mLilBadges;

    public LoadLilBadgeEvent(LILBadges mLilBadges) {
        this.mLilBadges = mLilBadges;
    }

    public LILBadges getmLilBadge() {
        return mLilBadges;
    }
}
