package in.securelearning.lil.android.learningnetwork.events;

/**
 * Created by Chaitendra 11-08-2017.
 */
public class LoadNewPostReceivedEvent {

    private final String mGroupId;

    public LoadNewPostReceivedEvent(String groupId) {
        this.mGroupId = groupId;
    }

    public String getGroupId() {
        return mGroupId;
    }
}
