package in.securelearning.lil.android.syncadapter.events;

/**
 * Created by Chaitendra on 06-06-2017.
 */

public class CourseDeleteEvent {
    private final String mId;

    public CourseDeleteEvent(String id) {
        mId = id;
    }

    public String getId() {
        return mId;
    }


}
