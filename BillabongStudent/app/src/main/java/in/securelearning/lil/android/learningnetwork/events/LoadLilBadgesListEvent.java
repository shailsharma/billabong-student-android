package in.securelearning.lil.android.learningnetwork.events;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.LILBadges;
import in.securelearning.lil.android.base.dataobjects.PostDataDetail;

/**
 * Created by Pushkar Raj 31-08-2016.
 */
public class LoadLilBadgesListEvent {
    private final ArrayList<LILBadges> mLilBadges;

    public LoadLilBadgesListEvent(ArrayList<LILBadges> mLilBadges) {
        this.mLilBadges = mLilBadges;
    }

    public ArrayList<LILBadges> getLilBadgesList() {
        return mLilBadges;
    }
}
