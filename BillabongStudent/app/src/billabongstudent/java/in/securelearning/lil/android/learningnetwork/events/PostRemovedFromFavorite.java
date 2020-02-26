package in.securelearning.lil.android.learningnetwork.events;

/**
 * Created by Chaitendra on 03-Aug-17.
 */

public class PostRemovedFromFavorite {
    private final String mAlias;

    public PostRemovedFromFavorite(String alias) {
        this.mAlias = alias;
    }

    public String getAlias() {
        return mAlias;
    }
}
