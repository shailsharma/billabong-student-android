package in.securelearning.lil.android.syncadapter.events;

/**
 * Created by Prabodh Dhabaria on 16-03-2017.
 */

public class UserProfileChangeEvent {
    private final String mId;
    private final String mName;

    public UserProfileChangeEvent(String id) {
        mId = id;
        mName = "";
    }

    public UserProfileChangeEvent(String id, String name) {
        mId = id;
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public String getId() {
        return mId;
    }
}
