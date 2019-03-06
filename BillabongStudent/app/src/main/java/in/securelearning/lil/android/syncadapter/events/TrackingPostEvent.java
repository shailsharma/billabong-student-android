package in.securelearning.lil.android.syncadapter.events;

/**
 * Created by Secure on 11-05-2017.
 */

public class TrackingPostEvent {
    private final String mObjectId;
    private final long mCreationTime;
    private final String mText;

    public TrackingPostEvent(String objectId, long creationTime, String text) {
        mObjectId = objectId;
        mCreationTime = creationTime;
        mText = text;
    }

    public String getObjectId() {
        return mObjectId;
    }


    public long getCreationTime() {
        return mCreationTime;
    }

    public String getText() {
        return mText;
    }
}
