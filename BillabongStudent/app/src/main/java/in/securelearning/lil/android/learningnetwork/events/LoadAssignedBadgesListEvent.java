package in.securelearning.lil.android.learningnetwork.events;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.AssignedBadges;
import in.securelearning.lil.android.base.dataobjects.LILBadges;

/**
 * Created by Pushkar Raj 31-08-2016.
 */
public class LoadAssignedBadgesListEvent {
    private final ArrayList<AssignedBadges> mAssignedBadges;

    public LoadAssignedBadgesListEvent(ArrayList<AssignedBadges> mAssignedBadges) {
        this.mAssignedBadges = mAssignedBadges;
    }

    public ArrayList<AssignedBadges> getAssignedBadges() {
        return mAssignedBadges;
    }
}
