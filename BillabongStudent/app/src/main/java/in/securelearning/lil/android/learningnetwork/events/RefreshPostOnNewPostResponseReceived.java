package in.securelearning.lil.android.learningnetwork.events;

/**
 * Created by Chaitendra on 21-Aug-17.
 */

public class RefreshPostOnNewPostResponseReceived {

    private final String mAlias;

    public RefreshPostOnNewPostResponseReceived(String alias) {
        this.mAlias = alias;
    }

    public String getAlias() {
        return mAlias;
    }
}
