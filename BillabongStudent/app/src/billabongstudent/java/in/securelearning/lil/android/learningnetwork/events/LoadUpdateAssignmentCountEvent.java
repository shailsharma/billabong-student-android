package in.securelearning.lil.android.learningnetwork.events;

import in.securelearning.lil.android.base.dataobjects.AssignmentResponse;

/**
 * Created by Pushkar Raj 31-08-2016.
 */
public class LoadUpdateAssignmentCountEvent {
    private final int count;

    public LoadUpdateAssignmentCountEvent(int count) {
        this.count=count;
    }

    public int getCount() {
        return count;
    }

}
