package in.securelearning.lil.android.syncadapter.events;

/**
 * Created by Secure on 20-07-2017.
 */

public class FavouriteResourceEvent {

    private final String mObjectId;

    public FavouriteResourceEvent(String objectId) {

        mObjectId = objectId;

    }

    public String getObjectId() {
        return mObjectId;
    }
}
