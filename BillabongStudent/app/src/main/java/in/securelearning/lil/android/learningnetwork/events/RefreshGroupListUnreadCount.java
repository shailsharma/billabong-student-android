package in.securelearning.lil.android.learningnetwork.events;

/**
 * Created by Chaitendra on 10-Aug-17.
 */

public class RefreshGroupListUnreadCount {
    private String mAlias;

    public RefreshGroupListUnreadCount(String alias) {
        this.mAlias = alias;

    }

    public String getAlias() {
        return mAlias;
    }
}
