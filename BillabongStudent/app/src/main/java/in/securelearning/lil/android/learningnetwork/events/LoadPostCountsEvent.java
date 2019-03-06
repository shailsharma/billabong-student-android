package in.securelearning.lil.android.learningnetwork.events;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.Group;

/**
 * Created by Pushkar Raj 31-08-2016.
 */
public class LoadPostCountsEvent {
    private final int mPostCounts;

    public LoadPostCountsEvent(int postCounts) {
        this.mPostCounts = postCounts;
    }

    public int getPostCounts() {
        return mPostCounts;
    }
}
