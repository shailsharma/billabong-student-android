package in.securelearning.lil.android.syncadapter.events;

import java.io.Serializable;

/**
 * Created by Prabodh Dhabaria on 16-02-2017.
 */

public class FavoriteAboutCourseUpdate implements Serializable {
    private final String mId;
    private final boolean isAdded;

    public FavoriteAboutCourseUpdate(String id, boolean isAdded) {
        mId = id;
        this.isAdded = isAdded;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public String getId() {
        return mId;
    }
}
