package in.securelearning.lil.android.learningnetwork.events;

/**
 * Created by Chaitendra on 05-Aug-17.
 */

public class EventLatestCommentAdded {

    private final String mAlias;

    public EventLatestCommentAdded(String alias) {
        this.mAlias = alias;
    }

    public String getAlias() {
        return mAlias;
    }
}
