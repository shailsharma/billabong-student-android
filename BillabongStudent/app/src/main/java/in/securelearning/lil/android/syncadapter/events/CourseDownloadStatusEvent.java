package in.securelearning.lil.android.syncadapter.events;

import java.io.Serializable;

/**
 * Created by Prabodh Dhabaria on 16-02-2017.
 */

public class CourseDownloadStatusEvent implements Serializable {
    private final String mId;

    public CourseDownloadStatusEvent(String id) {
        mId = id;
    }

    public String getId() {
        return mId;
    }
}
