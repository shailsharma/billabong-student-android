package in.securelearning.lil.android.learningnetwork.events;

import in.securelearning.lil.android.base.dataobjects.AssignedBadges;

/**
 * Created by Pushkar Raj 31-08-2016.
 */
public class LoadAssignedBadgeEvent {
    private final AssignedBadges mAssignedBadges;

    public LoadAssignedBadgeEvent(AssignedBadges mAssignedBadges) {
        this.mAssignedBadges = mAssignedBadges;
    }

    public AssignedBadges getmAssignedBadges() {
        return mAssignedBadges;
    }
}
